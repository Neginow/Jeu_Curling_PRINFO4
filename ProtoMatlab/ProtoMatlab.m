%% Prototype Matlab

% Le prototype se sert d'une image en entrée pour créer une cible sur
% ordinateur, repérer le jeton joué, créer le rond correspondant et
% calculer sa distance à la cible.

% Ici on se place dans le cas ou la cible est uniquement virtuelle.

%% Test avec une image
fond1 = imread("ImagesTrepiedTest\Fond.jpeg") ;
img = imread("ImagesTrepiedTest\Trepied1_3.jpeg") ;

figure; 
imshow(img)

gray_fond = rgb2gray(fond1) ;
gray_img = rgb2gray(img) ;

sansFond = imabsdiff(img, fond1) ;
sansFond = imadjust(rgb2gray(sansFond)) ;
figure ;
imshow(sansFond) ;


[centers, radii] = imfindcircles(sansFond, [10 20], 'ObjectPolarity', 'bright', 'Sensitivity', 0.95);

figure;
imshow(img) ;
viscircles(centers, radii, 'EdgeColor', 'b');

%% Logique de jeu

% Initialisation des variables
fond1 = imread("ImagesTrepiedTest\Fond.jpeg") ;
fond2 = imread("ImagesTrepiedTest\Fond2.jpeg") ;

centresPieces = [] ;
radiiPieces = [] ;

cible = [339, 207] ;
distancesCible = [] ;

% Affichage de la cible
figure ;
imshow(fond1) ;
hold on ;
plot(cible(1), cible(2), '+r', 'MarkerSize',15) ;
hold off ;


for i = 1:6
    filename = fullfile('ImagesTrepiedTest', sprintf('Trepied1_%d.jpeg', i));
    img = imread(filename);

    [nouveauxCentres, nouveauxRadii, nouvellesDistances] = tour(img, fond1, centresPieces, radiiPieces, cible, distancesCible, 1) ;
    centresPieces = nouveauxCentres ; 
    radiiPieces = nouveauxRadii ;
    distancesCible = nouvellesDistances ;
end

%% 2e série d'image
fond2 = imread("ImagesTrepiedTest\Fond2.jpeg") ;

centresPieces = [] ;
radiiPieces = [] ;

cible = [339, 207] ;
distancesCible = [] ;

% Affichage de la cible
figure ;
imshow(fond2) ;
hold on ;
plot(cible(1), cible(2), '+r', 'MarkerSize',15) ;
hold off ;


for i = 2:7
    filename = fullfile('ImagesTrepiedTest', sprintf('Trepied2_%d.jpeg', i));
    img = imread(filename);

    [nouveauxCentres, nouveauxRadii, nouvellesDistances] = tour(img, fond2, centresPieces, radiiPieces, cible, distancesCible, 2) ;
    centresPieces = nouveauxCentres ; 
    radiiPieces = nouveauxRadii ;
    distancesCible = nouvellesDistances ;
end
