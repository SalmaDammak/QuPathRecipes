/**
 * This script takes a csv file with tile information and prediction and creates annotations
 * corresponding to the tile location in coloured in a way that indicates its class.  
 *
 * Salma Dammak, 28 March 2022
 */
 
 //**************************************** INPUT PARAMETERS**********************************************************
 String positiveClassName = "Predicted_Positive";
 def positiveClassColour = ColorTools.packRGB(88, 254, 94); // light green
 String negativeClassName = "Predicted_Negative"; 
 def negativeClassColour = ColorTools.packRGB(254, 200, 254) // light pink
 //*******************************************************************************************************************
 
import java.io.BufferedReader;
import java.io.FileReader;
import qupath.lib.objects.PathAnnotationObject;
import qupath.lib.roi.RectangleROI;

// delete annotations from previous run. Uncomment when debugging/developing. 
// selectObjectsByClassification(positiveClassName);
// clearSelectedObjects();
// selectObjectsByClassification(negativeClassName);
// clearSelectedObjects();

def imageData = getCurrentImageData();
def imageName = imageData.getServer().getMetadata().getName();

// I have the csv files with the tile information saved in a folder called predictionTables in the project directory.
// The csv files are named as [imageName].csv and have the following columns
// slideName | x_location | y_location | width | height | prediction | truth 
// see helper MATLAB script for getting making these tables
def file = new File(buildFilePath(PROJECT_BASE_DIR, "predictionTables" ,imageName[0..-5]+".csv"))
print file

def csvReader = new BufferedReader(new FileReader(file))
row = csvReader.readLine()

while ((row = csvReader.readLine()) != null) {
    def rowContent = row.split(",")
    
    // read the tile information from the CSV
    double x_location = rowContent[1] as double;
    double y_location = rowContent[2] as double;
    double height = rowContent[3]as double;
    double width = rowContent[4] as double;
    boolean prediction = rowContent[5] as double == 1;
    
    def roi = new RectangleROI(x_location,y_location,width,height);
       
    def annotation = new PathAnnotationObject();

    if (prediction){  
        annotation = new PathAnnotationObject(roi, PathClassFactory.getPathClass(positiveClassName, positiveClassColour));
    } else {
        annotation = new PathAnnotationObject(roi, PathClassFactory.getPathClass(negativeClassName, negativeClassColour));
    }
    imageData.getHierarchy().addPathObject(annotation);
}
// merge annotations
//selectObjectsByClassification(positiveClassName);
//mergeSelectedAnnotations();

//selectObjectsByClassification(negativeClassName);
//mergeSelectedAnnotations();

print('Done!')
