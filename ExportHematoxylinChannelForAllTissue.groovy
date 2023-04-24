import qupath.lib.images.servers.LabeledImageServer
import qupath.lib.gui.images.servers.RenderedImageServer
import qupath.lib.common.GeneralTools
import qupath.lib.images.ImageData
import qupath.lib.images.servers.ImageServerMetadata
import qupath.lib.images.servers.TransformedServerBuilder
import qupath.lib.images.writers.TileExporter
import java.awt.image.BufferedImage

// Load image data to get all sort of information from it
def imageData = getCurrentImageData()

// Define output path relative to project with seperate folders for each slide, i.e., projectPath/folderName/slideFolder/
def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
String folderName = '0p2520_Hematoxylin'
def pathOutput = buildFilePath(PROJECT_BASE_DIR, folderName, name)
mkdirs(pathOutput)

// Tile side length in pixels
int tileSideLength = 224

// Define output resolution in calibrated units
double requestedPixelSize = 0.2520

// 1 is full resolution
double pixelSize = imageData.getServer().getPixelCalibration().getAveragedPixelSize()
double downsample = requestedPixelSize / pixelSize

// Create an ImageServer where the pixels are derived from annotations
def labelServer = new LabeledImageServer.Builder(imageData)
    .backgroundLabel(0, ColorTools.WHITE) 
    .downsample(downsample)    
    .addLabel('Positive', 1)     // Choose output labels (the order matters!)
    .multichannelOutput(false)  
    .build()
 
// Build deconvolved image server
def server = new TransformedServerBuilder(imageData.getServer())
    .deconvolveStains(imageData.getColorDeconvolutionStains())
    .build()	

// Create an exporter that requests corresponding tiles from the original & labeled image servers
new TileExporter(new ImageData<BufferedImage>(server))
    .downsample(downsample)     // Define export resolution
    .imageExtension('.tif')     // Define file extension for original pixels (often .tif, .jpg, '.png' or '.ome.tif')
    .tileSize(tileSideLength)   // Define size of each tile, in pixels
    .labeledServer(labelServer) // Define the labeled image server to use (i.e. the one we just built)
    .labeledImageSubDir('labels')
    .annotatedTilesOnly(true)   // If true, only export tiles if there is a (labeled) annotation present
    .overlap(0)                 // Define overlap, in pixel units at the export resolution
    .includePartialTiles(true)
    .writeTiles(pathOutput)     // Write tiles to the specified directory
    .channels("Hematoxylin")    // "Hematoxylin", "Eosin", "Residual"
     
// Get pixel side length for slide
def calibration = getCurrentServer().getPixelCalibration()

// Write information in JSON format
def map = [
  "name": getProjectEntry().getImageName(),
  "actualPixelWidth  ": calibration.pixelWidth,
  "actualPixelHeight ": calibration.pixelHeight,
  "averagePixelSize  ": pixelSize,
  "requestedPixelSize": requestedPixelSize,
  "tileSideLength":tileSideLength,
  "downsample":downsample
]
def gson = GsonTools.getInstance(true)

def JSONfile = new File(buildFilePath(PROJECT_BASE_DIR, folderName, name, 'calibration.json'))
JSONfile.text = gson.toJson(map)

print 'Done!'