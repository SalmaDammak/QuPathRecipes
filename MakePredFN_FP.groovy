import java.io.BufferedReader;
import java.io.FileReader;
import qupath.lib.objects.PathAnnotationObject;
import qupath.lib.roi.RectangleROI;

// delete annotations from previous run
selectObjectsByClassification("Predicted_Tumor");
clearSelectedObjects();
selectObjectsByClassification("Predicted_Stroma");
clearSelectedObjects();

def imageData = getCurrentImageData();
def imageName = imageData.getServer().getMetadata().getName();
def file = new File("G:/Users/sdammak/Experiments/LUSC Segment/116 slides/0p5040/7 Prepare for QuPath [2022-09-07_16.19.33]/Results/01 Experiment Section/"+imageName[0..-5]+".csv")
//def file = new File("C:/Users/sdammak/Desktop/SampleProj/"+imageName[0..-5]+"_predictions.csv")
print file

def csvReader = new BufferedReader(new FileReader(file))

//t sizePixels = 1000
row = csvReader.readLine()

while ((row = csvReader.readLine()) != null) {
    def rowContent = row.split(",")
    
    // read the tile information from the CSV
    double x_location = rowContent[1] as double;
    double y_location = rowContent[2] as double;
    double sideLength = rowContent[3] as double;
    boolean vdTP = rowContent[7] as double == 1;
    boolean vdFP = rowContent[8] as double == 1;
    boolean vdTN = rowContent[9] as double == 1;
    boolean vdFN = rowContent[10] as double == 1;
    
    def roi = new RectangleROI(x_location,y_location,sideLength,sideLength);
       
    def annotation = new PathAnnotationObject();

    if (vdTP){  
        annotation = new PathAnnotationObject(roi, PathClassFactory.getPathClass("TruePositive",ColorTools.GREEN));
    } else if (vdFP){
        annotation = new PathAnnotationObject(roi, PathClassFactory.getPathClass("FalsePositive",ColorTools.BLACK));
    } else if (vdTN){
        annotation = new PathAnnotationObject(roi, PathClassFactory.getPathClass("TrueNegative",ColorTools.CYAN));
    } else if (vdFN){
        annotation = new PathAnnotationObject(roi, PathClassFactory.getPathClass("FalseNegative",ColorTools.BLUE));
    }
    imageData.getHierarchy().addPathObject(annotation);
}
// merge annotations
selectObjectsByClassification("TruePositive");
mergeSelectedAnnotations();

selectObjectsByClassification("FalsePositive");
mergeSelectedAnnotations();

selectObjectsByClassification("TrueNegative");
mergeSelectedAnnotations();

selectObjectsByClassification("FalseNegative");
mergeSelectedAnnotations();

//selectObjectsByClassification("Predicted_Stroma", "Other");
//intersectSelectedAnnotations(); //https://forum.image.sc/t/intersecting-annotations-from-script/42889/3

print('Done!')
