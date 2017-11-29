import sys
import matplotlib.pyplot as plt
import re

# how to get the output file:
# 1) pipe output from "Java TestingClass type_of_alg debug" into a txt file
# 2) make sure to exit after timeout of servers
# 3) run this python script, and profit!

# example:
# java TestingClass p debug > p_out.txt


file = open(sys.argv[1], "r")

data = file.read().splitlines()

# assume this format:
# Server_name req $num proc $num
server_out = re.compile("[a-zA-Z0-9]+\sreq\s[0-9]+\sproc\s[0-9]+")
capacity_change = re.compile("Server ([a-zA-Z0-9]+) changing capacity from ([0-9]+) to ([0-9]+).*")
speed_change = re.compile("Server ([a-zA-Z0-9]+) changing processing speed from ([0-9]+) to ([0-9]+).*")

server_requests = {}
server_processed = {}
server_capacity = {}
server_speed = {}

server_tick = 0

for line in data:
    # we have regular output
    if server_out.match(line):
        temp = line.split()
        server = temp[0]
        req = temp[2]
        proc = temp[4]

        if server in server_requests.keys():
            server_requests[server][server_tick] = req
        else:
            server_requests[server] = {server_tick: req}

        if server in server_processed.keys():
            server_processed[server][server_tick] = proc
        else:
            server_processed[server] = {server_tick: proc}

        server_tick += 1

    if capacity_change.match(line):
        print(line)
        server = capacity_change.match(line).group(1)
        start = capacity_change.match(line).group(2)
        end = capacity_change.match(line).group(3)

        if server in server_capacity.keys():
            server_capacity[server][server_tick] = start
            server_capacity[server][server_tick+1] = end
        else:
            server_capacity[server] = {server_tick: start, server_tick+1: end}

    if speed_change.match(line):
        print(line)
        server = speed_change.match(line).group(1)
        start = speed_change.match(line).group(2)
        end = speed_change.match(line).group(3)

        if server in server_speed.keys():
            server_speed[server][server_tick] = start
            server_speed[server][server_tick+1] = end
        else:
            server_speed[server] = {server_tick: start, server_tick+1: end}

styles = ["-","--",":"]

plt.figure(1)
plt.subplot(111)
count = 0
for server in server_processed:
    sort = sorted(server_processed[server].items())
    x,y = zip(*sort)
    plt.plot(x, y, styles[count], label=server)
    count += 1
plt.legend(loc="upper left")
plt.title("Processed requests over time")

plt.figure(2)
plt.subplot(111)
count = 0
for server in server_requests:
    sort = sorted(server_requests[server].items())
    x,y = zip(*sort)
    plt.plot(x, y, styles[count], label=server)
    count += 1
plt.legend(loc="upper left")
plt.title("Waiting requests over time")

plt.figure(3)
plt.subplot(111)
count = 0
for server in server_capacity:
    sort = sorted(server_capacity[server].items())
    sort.append((server_tick,sort[-1][1]))
    x,y = zip(*sort)
    plt.plot(x, y, styles[count], label=server)
    count += 1
plt.legend(loc="upper left")
plt.title("Server capacity over time")

plt.figure(4)
plt.subplot(111)
count = 0
for server in server_speed:
    sort = sorted(server_speed[server].items())
    sort.append((server_tick,sort[-1][1]))
    x,y = zip(*sort)
    plt.plot(x, y, styles[count], label=server)
    count += 1
plt.legend(loc="upper left")
plt.title("Server speed over time")

plt.show()
