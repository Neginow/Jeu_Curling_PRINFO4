function [centresPieces, radiiPieces, distancesCible] = tour(img, fond, centresPieces, radiiPieces, cible, distancesCible, i)
% Prend en entrée les variables de la partie et renvoie la liste des centres des pièces jouées dans la partie mise à
% jour après le tour

% Affichage image + cible
figure(i);
imshow(img);
hold on ;
plot(cible(1), cible(2), '+r', 'MarkerSize',15) ;

% Recherche de la pièce
sansFond = imabsdiff(img, fond) ;
sansFond = imadjust(rgb2gray(sansFond)) ;

[center, radii] = imfindcircles(sansFond, [10 20], 'ObjectPolarity', 'bright', 'Sensitivity', 0.95);
% Elimination des chevauchements avec les anciennes
indices = indOverlap(center, radii(1), centresPieces, radiiPieces) ;
centresPieces(indices,:) = [] ;
radiiPieces(indices) = [] ;
distancesCible(indices) = [] ;

% Ajout de la pièce jouée
centresPieces(end+1,:) = center(1,:) ;
radiiPieces(end+1) = radii(1) ;
distancesCible(end+1) = distance2(center(1,:), cible) ;

disp(distancesCible(end)) ;

% Affichage de la solution
viscircles(centresPieces, radiiPieces, 'EdgeColor', 'b');

% Remplissage des cercles détectés pour l'esthétique
for i = 1:size(centresPieces, 1)
    theta = linspace(0, 2*pi, 100);
    x = centresPieces(i, 1) + radiiPieces(i) * cos(theta);
    y = centresPieces(i, 2) + radiiPieces(i) * sin(theta);
    fill(x, y, 'b', 'FaceAlpha', 0.8, 'EdgeColor', 'none'); % Rond plein bleu semi-transparent
end

hold off ;




end