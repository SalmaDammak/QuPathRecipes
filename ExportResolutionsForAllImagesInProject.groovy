/**
 * This script writes the resolution information, the pixel width and height in microns, for every image
 * in the project to a JSON file. 
 *
 * Copied and modified from:
 * https://github.com/qupath/2022-qupath-hackathon/discussions/2#discussioncomment-2649058
 */
 
def infoForAllImages = []

// loop through the images
for (def image in getProject().getImageList()) {
    // Create the ImageServer using try-with-resources to ensure 
    // it's closed even if something goes wrong
    try (def server = image.getServerBuilder().build()) {
        def calibration = server.getPixelCalibration()
        def resolutionInfo = [
          "name": image.getImageName(),
          "pixel_width": calibration.pixelWidth,
          "pixel_height": calibration.pixelHeight,
        ]
        infoForAllImages << resolutionInfo
    }
}

// write to JSON file
def gson = GsonTools.getInstance(true)
def JSONfile = new File(buildFilePath(PROJECT_BASE_DIR, 'ResolutionForAllImagesInProject.json'))
JSONfile.text = gson.toJson(infoForAllImages)

// print the information
println gson.toJson(infoForAllImages)