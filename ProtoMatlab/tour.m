function [centresPasDansFond, radiiPasDansFond] = tour(img, centresPasDansFond, radiiPasDansFond, centersFond)
% Prend en entrée les variables de la partie et renvoie la liste des centres des pièces jouées dans la partie mise à
% jour après le tour

% Amélioration du contraste
enhancedImage = imadjust(rgb2gray(img));


% Récupération des cercles 
% [centers, radii] = imfindcircles(enhancedImage, [90 180], 'ObjectPolarity', 'bright', 'Sensitivity', 0.95);

centers = [100, 200];
% centers(end+1, :) = [0,0] ;
radii = 20 ;
radii(end+1) = 20 ;

% Retrait des cercles communs avec le fond
tol = 2;
distances = distance2(centers, centersFond); % Matrice des distances entre cercles
estDansFond = any(distances < tol, 2); % Cercles communs au fond


% Récupération des centres et rayons pas dans le fond
new_center = centers(~estDansFond, :);
new_radii = radii(~estDansFond);


% Normalement à cette étape, il en reste un dans chaque liste

% Elimination des chevauchements
indices = indOverlap(new_center, new_radii(1), centresPasDansFond, radiiPasDansFond) ;
centresPasDansFond(indices,:) = [] ;
radiiPasDansFond(indices) = [] ;

% Ajout du centre de la pièce jouée
centresPasDansFond(end+1,:) = new_center(1,:) ;
radiiPasDansFond(end+1) = new_radii(1) ;


% Affichage de la solution
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


end

function dist = distance2(centers, centersFond)
n = size(centers,1);
m = size(centersFond,1);

dist = zeros(n, m);

for i = 1:n
    for j = 1:m
        dx = centers(i, 1) - centersFond(j, 1);
        dy = centers(i, 2) - centersFond(j, 2);
        
        dist(i, j) = sqrt(dx^2 + dy^2);
    end
end
end
