function displayToken(frame, circles)
    % Affiche une image avec plusieurs cercles bleus superposés
    % frame : Image d'entrée (matrice RGB)
    % circles : Liste de structures contenant le centre et le rayon des cercles détectés
    %           (ex: circles(i).Center = [x, y], circles(i).Radius = r)

    % Vérifier si des cercles ont été détectés
    if isempty(circles)
        disp('Aucun cercle détecté.');
        imshow(frame); % Afficher simplement l'image
        title('Aucun cercle détecté');
        return;
    end

    % Afficher l'image originale
    figure ;
    imshow(frame); 
    hold on;  % Garder l'image actuelle pour dessiner par-dessus

    % Tracer chaque cercle de la liste
    for i = 1:length(circles)
        center = circles(i).Center; % [x, y]
        radius = circles(i).Radius ;
        
        % Utiliser viscircles pour dessiner chaque cercle
        viscircles(center, radius, 'EdgeColor', 'b', 'LineWidth', 2);

        for i = 1:size(center, 1)
            theta = linspace(0, 2*pi, 100);
            x = center(i, 1) + radius(i) * cos(theta);
            y = center(i, 2) + radius(i) * sin(theta);
            fill(x, y, 'b', 'FaceAlpha', 0.8, 'EdgeColor', 'none'); % Rond plein bleu semi-transparent
        end
    end
    
    title('Image avec cercles détectés');
    hold off; % Libérer l'image pour d'autres modifications éventuelles
end
