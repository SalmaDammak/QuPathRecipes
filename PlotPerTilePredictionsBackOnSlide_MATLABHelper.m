%{
This is a helper script for PlotPerTilePredictionsBackOnSlide.groovy
It creates the CSV files that the groovy script uses.

Salma Dammak, 28 March 2022
%}
path =  'Experiment/results/path.mat';
load(path,'vsTilePaths','viPredictions','viTruth') % these qould be 

% Get slide names from paths
c1sParts = arrayfun(@(s) strsplit(s,'\'), vsTilePaths, 'UniformOutput', false);
c1chTileFilenamesNames = cellfun(@(c) c{end}, c1sParts, 'UniformOutput', false);
c1chSlideNames = cellfun(@(c) c(1:60),c1chTileFilenamesNames, 'UniformOutput', false);

% Get the rest of the information from the tile filenames 
c1chTileInfo = cellfun(@(c) c(62:end-4),c1chTileFilenamesNames, 'UniformOutput', false);
c1sX_Location = arrayfun(@(c) str2double(erase(regexp(c,"x=\d*", 'match'),"x=")),...
    string(c1chTileInfo), 'UniformOutput', false);
c1sY_location = arrayfun(@(c) str2double(erase(regexp(c,"y=\d*", 'match'),"y=")),...
    string(c1chTileInfo), 'UniformOutput', false);
c1sWidth   = arrayfun(@(c) str2double(erase(regexp(c,"w=\d*", 'match'),"w=")),...
    string(c1chTileInfo), 'UniformOutput', false);
c1sHeight  = arrayfun(@(c) str2double(erase(regexp(c,"h=\d*", 'match'),"h=")),...
    string(c1chTileInfo), 'UniformOutput', false);

% Make a CSV file for each slide
c1chUniqueSlides = unique(c1chSlideNames);

for iSlideIdx = 1:length(c1chUniqueSlides)
    viAllIndices = string(c1chSlideNames) == string(c1chUniqueSlides(iSlideIdx));
    tInfo = table(...
        c1chSlideNames(viAllIndices),...
        c1sX_Location(viAllIndices),...
        c1sY_location(viAllIndices),...
        c1sWidth(viAllIndices),...
        c1sHeight(viAllIndices),...
        viPredictions(viAllIndices),...
        viTruth(viAllIndices),...
        'VariableNames',{'slideName','x_location','y_location','width','height','prediction','truth'});
    writetable(tInfo,[c1chUniqueSlides{iSlideIdx},'.csv'])
    
end


