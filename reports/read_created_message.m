clc, clear all, close all;

% analyse message report
created_messages = fopen('default_scenario_CreatedMessagesReport.txt','rt');
created_messages_cell = textscan(created_messages, '%f%*s%f%s%s %*[^\n]', 'HeaderLines', 1);

created_message_info = zeros(length(created_messages_cell{3}), 4);
created_message_info(:,1:2) = cell2mat(created_messages_cell(1,1:2));
for i = 1 : length(created_messages_cell{3})
    created_message_info(i, 3) = str2double(extractAfter(created_messages_cell{3}(i),1));
    created_message_info(i, 4) = str2double(extractAfter(created_messages_cell{4}(i),1));
end

message_size_array = zeros(126,126);
for i = 1 : length(created_message_info)
    message_size_array(created_message_info(i,3)+1, created_message_info(i,4)+1) = created_message_info(i,2);
end

figure (1)
bar(created_message_info(:,1), created_message_info(:,2), 0.5);
grid on; grid minor;
title('Message Size vs Time');
xlabel('time (s)');
ylabel('message size (bytes)');
set(gcf, 'Units', 'Normalized', 'OuterPosition', [0, 0.04, 1, 0.96]);

figure (2)
bar3(message_size_array);
grid on; grid minor;
title('Created Messages');
xlabel('destination node');
ylabel('source node');
zlabel('message size (bytes)');
pbaspect([1 1.5 1]);
set(gcf, 'Units', 'Normalized', 'OuterPosition', [0, 0.04, 1, 0.96]);