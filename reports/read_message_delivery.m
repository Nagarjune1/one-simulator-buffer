clc, clear all, close all;

% analyse message report
message_delivery = fopen('default_scenario_MessageDeliveryReport.txt','rt');
message_delivery_array = cell2mat(textscan(message_delivery, '%f%f%f%f %*[^\n]', 'HeaderLines', 1));
message_delivery_array(:,4) = message_delivery_array(:,4).*100;
% analyse message report
message_delay = fopen('default_scenario_MessageDelayReport.txt','rt');
message_delay_array = cell2mat(textscan(message_delay, '%f%f %*[^\n]', 'HeaderLines', 1));
% analyse message report
message = fopen('default_scenario_MessageReport.txt','rt');
message_cell = textscan(message, '%s%f%f %*[^\n]', 'HeaderLines', 1);

message_info = zeros(length(message_cell{1}), 4);
message_info(:,2:3) = cell2mat(message_cell(1,2:3));
for i = 1 : length(message_cell{1})
    message_info(i, 1) = str2double(extractAfter(message_cell{1}(i),1));
    message_info(i, 4) = message_info(i, 3) - message_info(i, 2);
end

figure (1)

subplot(2,1,1);
plot(message_delivery_array(:,1),message_delivery_array(:,3));
grid on; grid minor;
title('Delivered Messages vs Time');
xlabel('time (s)');
ylabel('Delivered Messages');
subplot(2,1,2);
plot(message_delivery_array(:,1),message_delivery_array(:,4));
grid on; grid minor;
title('Percentage of created message delivered vs Time');
xlabel('time (s)');
ylabel('Percentage of created message delivered (%)');
set(gcf, 'Units', 'Normalized', 'OuterPosition', [0, 0.04, 1, 0.96]);

figure (2)
subplot(1,2,1);
plot(message_delay_array(:,1), message_delay_array(:,2));
grid on; grid minor;
xlabel('message delay (s)');
ylabel('cumulative probability');
subplot(1,2,2);
bar(message_info(:,1), message_info(:,4));
grid on; grid minor;
xlabel('message ID');
ylabel('transfer time (s)');
set(gcf, 'Units', 'Normalized', 'OuterPosition', [0, 0.04, 1, 0.96]);