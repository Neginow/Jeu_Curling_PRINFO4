function indices = indOverlap(centerNew, r1,centers, radiis)

distances = distance2(centerNew, centers) ;
indices = [] ;
A_NewCircle = pi*r1^2 ;

for i = 1:length(radiis)
    d = distances(i) ;
    r2 = radiis(i) ;
    
    term1 = acos( (d^2 + r1^2 - r2^2) / (2 * d * r1) );
    term2 = acos( (d^2 + r2^2 - r1^2) / (2 * d * r2) );

    term3 = (-d + r1 + r2) * (d + r1 - r2) * (d - r1 + r2) * (d + r1 + r2);
    term3 = sqrt(term3) / 2;

    A_intersection = (r1^2 * term1) + (r2^2 * term2) - term3;

    if A_intersection > 0.05*A_NewCircle
        indices(end+1) = i ;
    end
    
end

end