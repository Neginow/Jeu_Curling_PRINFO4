%% Prototype Matlab

% Le prototype se sert d'une image en entrée pour créer une cible sur
% ordinateur, repérer le jeton joué, créer le rond correspondant et
% calculer sa distance à la cible.

% Ici on se place dans le cas ou la cible est uniquement virtuelle.

%% Cas Image1

% Lire l'image
img1 = imread('Images/Image1.jpg'); % Remplace 'nom_image.jpg' par le nom de ton fichier

% Convertir l'image en niveaux de gris
gray_img1 = rgb2gray(img1);

% Appliquer une transformation de Hough pour détecter les cercles
% Ajuster les valeurs de rayon min et max pour s'adapter à la taille de la pièce
[centers, radii] = imfindcircles(gray_img1, [90 200], 'ObjectPolarity', 'bright', 'Sensitivity', 0.95);

% On ne garde que le cercle le plus grand car le contour de la pièce est
% le cercle le plus grand de l'image.
[~,indMax] = max(radii) ;

% Print les coordonnées du centre.
centers(1,:)

% Afficher l'image, le centre et le cercle détecté.
figure;
imshow(img1);
hold on;
viscircles(centers(indMax, :), radii(indMax), 'EdgeColor', 'b');
plot(centers(1,1), centers(1,2), 'r+', 'MarkerSize', 15, 'LineWidth', 2); % Seulement pour vérifier l'algo mais pas utile dans le proto.
hold off;


%% Cas trépied

%% Traitement du fond
fond = imread("Images\Image1.jpg");
enhancedFond = imadjust(rgb2gray(fond));

[centersFond, radiiFond] = imfindcircles(enhancedFond, [90 180], 'ObjectPolarity', 'bright', 'Sensitivity', 0.95);

figure;
imshow(fond) ;
viscircles(centersFond, radiiFond, 'EdgeColor', 'b');

%% Traitement de l'image
img = imread("Images\Image1.jpg");
enhancedImage = imadjust(rgb2gray(img));

figure; 
imshow(enhancedImage);

[centers, radii] = imfindcircles(enhancedImage, [90 180], 'ObjectPolarity', 'bright', 'Sensitivity', 0.95);

figure;
imshow(img) ;
viscircles(centers, radii, 'EdgeColor', 'b');

%% Retrait des cercles communs avec le fond

% Tolérance de deux pixels pour trouver les centres communs
tol = 2;
distances = pdist2(centers, centersFond); % Matrice des distances entre cercles
estDansFond = any(distances < tol, 2); % Cercles communs au fond

% Récupération des centres et rayons pas dans le fond
centresPasDansFond = centers(~estDansFond, :);
radiiPasDansFond = radii(~estDansFond);

%% Affichage de la solution
figure;
imshow(img);
hold on;
viscircles(centresPasDansFond, radiiPasDansFond, 'EdgeColor', 'b');

% Remplissage des cercles détectés pour l'esthétique
for i = 1:size(centresPasDansFond, 1)
    theta = linspace(0, 2*pi, 100);
    x = centresPasDansFond(i, 1) + radiiPasDansFond(i) * cos(theta);
    y = centresPasDansFond(i, 2) + radiiPasDansFond(i) * sin(theta);
    fill(x, y, 'b', 'FaceAlpha', 0.3, 'EdgeColor', 'none'); % Rond plein bleu semi-transparent
end

hold off;
