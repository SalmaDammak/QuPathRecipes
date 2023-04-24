import qupath.lib.gui.tools.MeasurementExporter
import qupath.lib.objects.PathAnnotationObject

// Get the list of all images in the current project
def project = getProject()
def imagesToExport = project.getImageList()

// Separate each measurement value in the output file with a tab ("\t")
def separator = ","

// Choose the columns that will be included in the export
// Note: if 'columnsToInclude' is empty, all columns will be included
//def columnsToInclude = new String[]{"Image", "Class", "Num Tumor", "Centroid X Âµm", "Centroid Y Âµm"}

// Choose the type of objects that the export will process
// Other possibilities include:
//    1. PathAnnotationObject
//    2. PathDetectionObject
//    3. PathRootObject
// Note: import statements should then be modified accordingly
def exportType = PathAnnotationObject.class

// Choose your *full* output path
def outputPath = buildFilePath(PROJECT_BASE_DIR, "measurements_0p2520.csv")
def outputFile = new File(outputPath)

// Create the measurementExporter and start the export
def exporter  = new MeasurementExporter()
                  .imageList(imagesToExport)            // Images from which measurements will be exported
                  .separator(separator)                 // Character that separates values
                  //.includeOnlyColumns(columnsToInclude) // Columns are case-sensitive
                  .exportType(exportType)               // Type of objects to export
                  .filter(obj -> obj.getPathClass() == getPathClass("Predicted_Positive"))    // Keep only objects with class 'Tumor'
                  .exportMeasurements(outputFile)        // Start the export process

print "Done!"