function [centresPieces, radiisPieces, distancesCible] = tour(img, centresPieces, radiisPieces, cible, distancesCible, k)
    % Affichage image + cible
    figure(k);
    imshow(img);
    hold on;
    plot(cible(1), cible(2), '+g', 'MarkerSize', 15);

    % Recherche de la pièce
    circle = detectToken(img); % Detecte le jeton (circle est une structure)

    % Vérification du type et de l'existence de 'Center' et 'Radius' dans circle
    if ~isstruct(circle) || ~isfield(circle, 'Center') || isempty(circle.Center) || ~isfield(circle, 'Radius') || isempty(circle.Radius)
        disp('Aucun cercle détecté ou format incorrect.');
        return; % Si circle n'est pas valide, on quitte la fonction
    end

    % Elimination des chevauchements avec les anciennes
    indices = indOverlap(circle, centresPieces); % Passe circle comme une structure
    centresPieces(indices,:) = []; % Élimine les cercles qui chevauchent
    radiisPieces(indices,:) = []; % Élimine les rayons qui correspondants
    distancesCible(indices) = []; % Élimine les distances correspondantes

    % Ajout de la pièce jouée
    centresPieces(end+1) = [circle.Center(1), circle.Center(2)];
    radiisPieces(end+1) = circle.Radius;  % Ajoute le rayon sous forme de champ
    distancesCible(end+1) = distance2([circle.Center], cible); % Calcul de la distance

    % Détermination de la couleur
    if mod(k, 2) == 0
        couleur = 'r';
    else
        couleur = 'b';
    end

    % Affichage de la solution
    viscircles(centresPieces, radiisPieces, 'EdgeColor', couleur);

    % Remplissage des cercles détectés pour l'esthétique
    for i = 1:length(centresPieces)
        center = centresPieces(i) ; % [x, y]
        radius = radiisPieces(i) ;
        
        % Utiliser viscircles pour dessiner chaque cercle
        viscircles(center, radius, 'EdgeColor', 'b', 'LineWidth', 2);

        for i = 1:size(center, 1)
            theta = linspace(0, 2*pi, 100);
            x = center(i, 1) + radius(i) * cos(theta);
            y = center(i, 2) + radius(i) * sin(theta);
            fill(x, y, 'b', 'FaceAlpha', 0.8, 'EdgeColor', 'none'); % Rond plein bleu semi-transparent
        end
    end

    hold off;
end
