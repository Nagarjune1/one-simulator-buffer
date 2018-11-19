clc, clear all, close all;
% analyse message count characteristic across nodes
message_count = fopen('default_scenario_MessageCountReport.txt','rt');
message_count_array = cell2mat(textscan(message_count, repmat('%f',[1,127])));
fclose(message_count);
time = message_count_array(:,1);
message_count_array = message_count_array(:,2:end);
message_count_array_diff = [zeros(1, 126); diff(message_count_array)];

% plot message count occupancy
bar3(time, message_count_array);
grid on; grid minor;
ylim([0,1000])
title('Message Count across nodes');
xlabel('node id');
ylabel('time (s)');
zlabel('message count');
zlim([0, 10]);
pbaspect([1 1.5 1]);

% Enlarge figure to full screen.
set(gcf, 'Units', 'Normalized', 'OuterPosition', [0, 0.04, 1, 0.96]);