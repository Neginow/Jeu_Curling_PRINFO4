function circle = detectToken(frame)
    % Initialisation des matrices
    grayFrame = rgb2gray(frame); % Conversion en niveaux de gris
    morphKernel = strel('disk', 10); % Élément structurant en ellipse (équivalent à Size(10,10))

    % Application du filtre Blackhat
    blackHat = imtophat(grayFrame, morphKernel);
    grayFrame = imsubtract(grayFrame, blackHat);

    % Augmenter le contraste - Fonction utilisateur
    m = increaseContrast(blackHat, 2.0, 5.0);

    % Flou gaussien
    blurred = imgaussfilt(m, 1.5); % Sigma = 1.5, taille par défaut de 7x7

    % Détection des cercles avec Hough
    [centers, radii] = imfindcircles(blurred, [15, 200], ...
            'ObjectPolarity', 'bright', 'Sensitivity', 0.9, 'EdgeThreshold', 0.1);

    % Facteur d'échelle parce qu'on récupère le cercle intérieur
    radii = radii * 1.3;

    % Vérification des résultats
    if ~isempty(centers)
        % Trouver l'index du rayon le plus grand
        [~, idx_max] = max(radii);  % idx_max donne l'index du rayon maximal
        
        % Utiliser l'index pour obtenir les coordonnées et le rayon du cercle avec le plus grand rayon
        x = centers(idx_max, 1);
        y = centers(idx_max, 2);
        radius = radii(idx_max); % Rayon détecté avec le plus grand rayon
        
        % Retourner la structure Circle
        circle = struct('Center', [x, y], 'Radius', radius);
    else
        % Si aucun cercle n'est détecté, retourner une structure vide avec des champs
        circle = struct('Center', [], 'Radius', []);
        disp(circle); % Affiche la structure de circle
    end
end


function result = increaseContrast(frame, alpha, beta)
    % Augmente le contraste et ajuste la luminosité
    % frame : Image d'entrée (matrice)
    % alpha : Facteur de contraste (gain multiplicatif)
    % beta  : Décalage (ajout à l'intensité des pixels)

    % Conversion en double pour éviter les saturations lors des calculs
    frame = double(frame);

    % Appliquer les transformations : intensité = alpha * intensité + beta
    result = alpha * frame + beta;

    % Clipper les valeurs pour qu'elles restent dans les limites valides (0-255)
    result = uint8(max(min(result, 255), 0));
end