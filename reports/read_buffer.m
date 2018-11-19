clc, clear all, close all;

% analyse buffer occupancy mean and variance
bufferstats = fopen('default_scenario_BufferOccupancyReport.txt','rt');
bufferstats_array = cell2mat(textscan(bufferstats, repmat('%f',[1,3])));
fclose(bufferstats);
% analyse buffer characteristic across nodes
buffer = fopen('default_scenario_BufferOccupancySpecificReport.txt','rt');
buffer_array = cell2mat(textscan(buffer, repmat('%f',[1,127])));
fclose(buffer);
time = buffer_array(:,1);
buffer_array = buffer_array(:,2:end);
buffer_array_diff = [zeros(1, 126); diff(buffer_array)];

figure (1)
plot(bufferstats_array(:,1), bufferstats_array(:,2)); hold on;
plot(bufferstats_array(:,1), bufferstats_array(:,3));
grid on; grid minor;
title('Buffer Occupancy vs Time');
xlabel('time (s)');
ylabel('Mean and Variance (%)');
legend('mean','variance');

figure (2)

subplot(2,2,1); bar3(time, buffer_array(:,1:40));
grid on; grid minor;
ylim([0,1000]);
title('Pedestrian 1');
xlabel('node id');
ylabel('time (s)');
zlabel('buffer occupancy (%)');
pbaspect([1 1.5 1]);

subplot(2,2,2); bar3(time, buffer_array(:,41:80));
grid on; grid minor;
ylim([0,1000]);
title('Car');
xlabel('node id');
ylabel('time (s)');
zlabel('buffer occupancy (%)');
pbaspect([1 1.5 1]);

subplot(2,2,3); bar3(time, buffer_array(:,81:120));
grid on; grid minor;
ylim([0,1000]);
title('Pedestrian 2');
xlabel('node id');
ylabel('time (s)');
zlabel('buffer occupancy (%)');
pbaspect([1 1.5 1]);

subplot(2,2,4); bar3(time, buffer_array(:,121:end));
grid on; grid minor;
ylim([0,1000]);
title('Trams');
xlabel('node id');
ylabel('time (s)');
zlabel('buffer occupancy (%)');
pbaspect([1 1.5 1]);

% Enlarge figure to full screen.
set(gcf, 'Units', 'Normalized', 'OuterPosition', [0, 0.04, 1, 0.96]);

% plot buffer occupancy
figure (3)

subplot(1,2,1); bar3(time, buffer_array);
grid on; grid minor;
ylim([0,1000])
title('Buffer Occupancy across nodes', 'FontSize', 15);
xlabel('node id', 'FontWeight', 'bold');
ylabel('time (s)', 'FontWeight', 'bold');
zlabel('buffer occupancy (%)', 'FontWeight', 'bold');
pbaspect([1 1.5 1]);

subplot(1,2,2); bar3(time, buffer_array_diff);
grid on; grid minor;
ylim([0,1000])
title('Buffer Occupancy Changes across nodes', 'FontSize', 15);
xlabel('node id', 'FontWeight', 'bold');
ylabel('time (s)', 'FontWeight', 'bold');
zlabel('buffer occupancy (%)', 'FontWeight', 'bold');
pbaspect([1 1.5 1]);

% Enlarge figure to full screen.
set(gcf, 'Units', 'Normalized', 'OuterPosition', [0, 0.04, 1, 0.96]);