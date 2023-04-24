def imageData = getCurrentImageData()
def server = QP.getCurrentImageData().getServer()
def requestFull = RegionRequest.createInstance(server, 100)

def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName()) + ".png"
def path = pathOutput = buildFilePath(PROJECT_BASE_DIR, "DownsampledImages", name)
mkdirs(buildFilePath(PROJECT_BASE_DIR, "DownsampledImages"))
    
writeImageRegion(server, requestFull, path)
