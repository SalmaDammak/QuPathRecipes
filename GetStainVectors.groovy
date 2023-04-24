/*
 Modified from script by igor Zindy:
 https://forum.image.sc/t/passing-custom-stain-vectors-to-third-party-application/29558/6
 */
import qupath.lib.scripting.QP
import java.nio.charset.StandardCharsets

import java.nio.file.Files 
import java.nio.file.Paths
import org.apache.commons.io.IOUtils

import qupath.lib.io.GsonTools
import com.google.gson.Gson

import qupath.lib.color.ColorDeconvolutionStains
import qupath.lib.color.StainVector

def write_stains() {
    
    def imageData = getCurrentImageData()
    def stains = imageData.getColorDeconvolutionStains()
    
    def server = QP.getCurrentImageData().getServer()

    //*********Get a JSON filename automatically based on naming scheme 
    def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName()) + ".json"
    def path = pathOutput = buildFilePath(PROJECT_BASE_DIR, "StainVectors", name)
    mkdirs(buildFilePath(PROJECT_BASE_DIR, "StainVectors"))

    println path
    
    boolean prettyPrint = true
    def gson = GsonTools.getInstance(prettyPrint)

    // write (save) the json file
    try (Writer writer = new FileWriter(path)) {
        gson.toJson(stains, writer);
    }
}    

def read_stains() {
    def server = QP.getCurrentImageData().getServer()

    //*********Get a JSON filename automatically based on naming scheme 
    def path = GeneralTools.toPath(server.getURIs()[0]).toString()
    path = path[0..<path.lastIndexOf('.')]+"_stains.json";
    println path;

    def JSONfile = new File(path)
    if (!JSONfile.exists()) {
        println "No stains file for this image..."
        return
    }

    Gson gson = new Gson(); 
    map = gson.fromJson(JSONfile.text, Map.class);
    StainVector stain1 = StainVector.createStainVector(map.stain1.name, map.stain1.r, map.stain1.g, map.stain1.b)
    StainVector stain2 = StainVector.createStainVector(map.stain2.name, map.stain2.r, map.stain2.g, map.stain2.b)
    StainVector stain3 = StainVector.createStainVector(map.stain3.name, map.stain3.r, map.stain3.g, map.stain3.b)
    
    ColorDeconvolutionStains stains = new ColorDeconvolutionStains(map.name, stain1, stain2, stain3, map.maxRed, map.maxGreen, map.maxBlue);
    
    return stains
}

//Here we write the stains
write_stains()

//Here we read the stains
//stains = read_stains()
//println stains