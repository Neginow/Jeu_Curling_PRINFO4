%% Prototype Matlab

% Le prototype se sert d'une image en entrée pour créer une cible sur
% ordinateur, repérer le jeton joué, créer le rond correspondant et
% calculer sa distance à la cible.

% Ici on se place dans le cas ou la cible est uniquement virtuelle.

%% Traitement du fond
fond = imread("Images\Image1.jpg");
enhancedFond = imadjust(rgb2gray(fond));

figure; 
imshow(enhancedFond);

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
    fill(x, y, 'b', 'FaceAlpha', 0.8, 'EdgeColor', 'none'); % Rond plein bleu semi-transparent
end

hold off;

%% Logique tour 2

