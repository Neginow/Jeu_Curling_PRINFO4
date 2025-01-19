%% Prototype Matlab

%% Test de la détection sur une image

img = imread("Images\down_red_low_clear2.jpg") ;

[circle, ~, ~] = detectToken(img) ;
displayToken(img, circle) ;


%% Test de la détection sur la base d'images

Pictures = dir("Images/*.jpg") ;

for i = 1:length(Pictures)
    % Lecture de l'image
    img = imread(strcat("Images/", Pictures(i).name)) ;
    
    [circle, ~, ~] = detectToken(img) ;
    displayToken(img, circle) ;
end

%% Logique du jeu

Pictures = dir("Images/*.jpg") ;

centresXpieces = [] ;
centresYpieces = [] ;
radiisPieces = [] ;
distancesCible = [] ;

cible = [300, 250] ;

for i = 1:16
    % Lecture de l'image
    img = imread(strcat("Images/", Pictures(i).name)) ;
    
    [centresXpieces, centresYpieces, radiisPieces, distancesCible] = tour(img, centresXpieces, centresYpieces, radiisPieces, cible, distancesCible, i) ;
end

