function indices = indOverlap(centre, rayon, centresXpieces, centresYpieces, radiisPieces)
    % centerNew : Structure de type circle (Center = [x, y], Radius = r1)
    % centers : Liste de structures de type circle, chaque structure ayant Center et Radius
    
    distances = [] ;

    % Calcul des distances entre le centre de centerNew et les centres des cercles dans centers
    for i =1:length(centresXpieces)
        distances(end+1) = distance2(centre, [centresXpieces(i), centresYpieces(i)]);
    end
    % distances(i) contient la distance entre centerNew et centers(i)

    indices = []; % Initialiser les indices
    A_NewCircle = pi * rayon^2; % Aire du cercle de centerNew

    for i = 1:length(centresXpieces)
        % Récupérer le rayon du cercle existant dans la liste
        r2 = radiisPieces(i) ;

        % Récupérer la distance entre le centre de centerNew et celui du cercle existant
        d = distances(i);

        % Vérifier si les cercles se chevauchent
        if d < rayon + r2 % Si la distance est inférieure à la somme des rayons
            % Calcul des termes pour l'aire d'intersection des cercles
            if d == 0 && rayon == r2
                % Si les cercles sont identiques
                A_intersection = pi * circleNew.Radius^2;
            else
                term1 = acos((d^2 + rayon^2 - r2^2) / (2 * d * rayon));
                term2 = acos((d^2 + r2^2 - rayon^2) / (2 * d * r2));
                term3 = (-d + rayon + r2) * (d + rayon - r2) * (d - rayon + r2) * (d + rayon + r2);
                term3 = sqrt(term3) / 2;

                % Calcul de l'aire d'intersection
                A_intersection = (rayon^2 * term1) + (r2^2 * term2) - term3;
            end

            % Vérification si l'aire d'intersection est significative par rapport à l'aire du cercle de centerNew
            if A_intersection > 0.05 * A_NewCircle
                indices(end+1) = i; % Ajouter l'indice du cercle intersecté
            end
        end
    end
end
