function dist = distance2(c1, c2)

dx = c1(1) - c2(1);
dy = c1(2) - c2(2);
        
dist = sqrt(dx^2 + dy^2);

end