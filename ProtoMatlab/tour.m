function [centresXpieces, centresYpieces, radiisPieces, distancesCible] = tour(img, centresXpieces, centresYpieces, radiisPieces, cible, distancesCible, k)
    % Affichage image + cible
    figure(k);
    imshow(img);
    hold on;
    plot(cible(1), cible(2), '+g', 'MarkerSize', 15);

    % Recherche de la pièce
    [~, centre, rayon]  = detectToken(img); % Detecte le jeton (circle est une structure)

    % Elimination des chevauchements avec les anciennes
    indices = indOverlap(centre, rayon, centresXpieces, centresYpieces, radiisPieces); % Passe circle comme une structure
    centresXpieces(indices,:) = [];% Élimine les cercles qui chevauchent
    centresYpieces(indices,:) = [];
    radiisPieces(indices,:) = []; % Élimine les rayons qui correspondants
    distancesCible(indices) = []; % Élimine les distances correspondantes

    % Ajout de la pièce jouée
    centresXpieces(end+1) = centre(1) ;
    centresYpieces(end+1) = centre(2) ;
    radiisPieces(end+1) = rayon ;  % Ajoute le rayon sous forme de champ
    distancesCible(end+1) = distance2(centre, cible); % Calcul de la distance


    % Remplissage des cercles détectés pour l'esthétique
    couleur = 'b' ;

    for i = 1:length(centresXpieces)
        center = [centresXpieces(i), centresYpieces(i)] ;
        radius = radiisPieces(i) ;
        
        % Utiliser viscircles pour dessiner chaque cercle
        viscircles(center, radius, 'EdgeColor', couleur, 'LineWidth', 2);

        for i = 1:size(center, 1)
            theta = linspace(0, 2*pi, 100);
            x = center(i, 1) + radius(i) * cos(theta);
            y = center(i, 2) + radius(i) * sin(theta);
            fill(x, y, couleur, 'FaceAlpha', 0.8, 'EdgeColor', 'none'); % Rond plein semi-transparent
        end
    end

    hold off;
end
