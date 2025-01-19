function dist = distance2(c1, c2)
n = size(c1,1);
m = size(c2,1);

dist = zeros(n, m);

for i = 1:n
    for j = 1:m
        dx = c1(i, 1) - c2(j, 1);
        dy = c1(i, 2) - c2(j, 2);
        
        dist(i, j) = sqrt(dx^2 + dy^2);
    end
end
end