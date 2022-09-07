/**
 * This script exports the information used for tiling. This is specifically for unit testing and tile resizing
 * post-processing outside QuPath.
 *
 * Salma Dammak, 24 Aug 2022
 */
 
//**************************************** INPUT PARAMETERS********************************************************** 
// Uncomment to specify the tile side length based on a target resolution and tile size in pixels
double modeTileSideLength_pixel = 224
double modeScanResolution_micronsPerPixel = 0.2520
double targetTileSideLengthSize_microns = modeTileSideLength_pixel * modeScanResolution_micronsPerPixel

// uncomment to just specify a specific tile side length
//double targetTileSideLengthSize_microns = 57           
//*******************************************************************************************************************

import qupath.lib.images.servers.LabeledImageServer
def imageData = getCurrentImageData()

// Calculate tile side length in pixels
double resolution_micronsPerPixel = imageData.getServer().getPixelCalibration().getAveragedPixelSize()
double dRequiredTileSideLength_pixels = targetTileSideLengthSize_microns / resolution_micronsPerPixel
int    iRequiredTileSideLength_pixels = Math.round(dRequiredTileSideLength_pixels);

// Create output folder
def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
//String folderName = "TileSideLengthOf_" + targetTileSideLengthSize_microns.toString() + "_microns"
String folderName = "57microns_NoResize"
def pathOutput = buildFilePath(PROJECT_BASE_DIR, folderName, name)
mkdirs(pathOutput)

// Get pixel side length for slide
def calibration = getCurrentServer().getPixelCalibration()

// in JSON format
def map = [
  "name": getProjectEntry().getImageName(),
  "pixel_width": calibration.pixelWidth,
  "pixel_height": calibration.pixelHeight,
  "modeTileSideLength_pixel": modeTileSideLength_pixel,
  "modeScanResolution_micronsPerPixel": modeScanResolution_micronsPerPixel,
  "targetTileSideLengthSize_microns": targetTileSideLengthSize_microns,
  "dRequiredTileSideLength_pixels": dRequiredTileSideLength_pixels,
  "iRequiredTileSideLength_pixels": iRequiredTileSideLength_pixels
]

def gson = GsonTools.getInstance(true)

def JSONfile = new File(buildFilePath(PROJECT_BASE_DIR, folderName, name, 'calibration.json'))
JSONfile.text = gson.toJson(map)

print 'Done!'