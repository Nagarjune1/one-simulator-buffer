clc, clear all, close all;

% analyse message report
delivered_messages = fopen('default_scenario_DeliveredMessagesReport.txt','rt');
delivered_messages_cell = textscan(delivered_messages, '%f%*s%f%f%f%s%s %*[^\n]', 'HeaderLines', 1);

delivered_messages_info = zeros(length(delivered_messages_cell{5}), 6);
delivered_messages_info(:,1:4) = cell2mat(delivered_messages_cell(1,1:4));
for i = 1 : length(delivered_messages_cell{5})
    delivered_messages_info(i, 5) = str2double(extractAfter(delivered_messages_cell{5}(i),1));
    delivered_messages_info(i, 6) = str2double(extractAfter(delivered_messages_cell{6}(i),1));
end

message_size_array = zeros(126,126);
for i = 1 : length(delivered_messages_info)
    message_size_array(delivered_messages_info(i,5)+1, delivered_messages_info(i,6)+1) = delivered_messages_info(i,2);
end

figure (1)
bar(delivered_messages_info(:,1), delivered_messages_info(:,2), 0.5);
grid on; grid minor;
xlabel('time (s)');
ylabel('message size');
set(gcf, 'Units', 'Normalized', 'OuterPosition', [0, 0.04, 1, 0.96]);

figure (2)
bar3(message_size_array);
grid on; grid minor;
title('Delivered Messages');
xlabel('destination node');
ylabel('source node');
zlabel('message size (bytes)');
pbaspect([1 1.5 1]);
set(gcf, 'Units', 'Normalized', 'OuterPosition', [0, 0.04, 1, 0.96]);