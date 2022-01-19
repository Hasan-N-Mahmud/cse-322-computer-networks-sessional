
import matplotlib.pyplot as plt
for t in (0, 5, 10):

    Throughput = []
    Average_delay = []
    Packet_delivery_ratio = []
    Packet_drop_ratio = []
    area = []
    nn = []
    nf = []
    for index in range(0, 5):
        file_name = "trace"+str(t+index)+".tr"
        print(file_name)
        file = open(file_name, "r")
        total_packet_sent = 0
        total_packet_received = 0
        total_sent_bit = 0
        packet_drop = 0
        sim_start_time = 0
        start_time = {}
        stop_time = {}
        total_delay = 0
        first_send = False
        sim_start_time = 1000000
        sim_end_time = 0
        while (True):
            line = file.readline()
            if line == "":
                break
            strg = line.split()
       
            if sim_start_time > float(strg[1]):
                sim_start_time = float(strg[1])
            if strg[3] == "AGT" and strg[6] == "tcp":
                if strg[0] == "s":
                    start_time[strg[5]] = float(strg[1])
                    total_packet_sent += 1
                elif strg[0] == "r":
                    delay = float(strg[1]) - start_time[strg[5]]
                    total_delay += delay
                    total_sent_bit += ((int(strg[7])-20)*8)
                    total_packet_received += 1

            if strg[0] == 'D' and strg[6] == 'tcp':
                packet_drop += 1
            
            sim_end_time = float(strg[1])
        #print(i)
        if t == 0:
            area.append(250*(index+1))

        elif t == 5:
            nn.append(20*(index+1))
        else:
            nf.append(10*(index+1))

        #print(start_time)
        print(f'Drop: {packet_drop}')
        throughput = total_sent_bit/(sim_end_time-sim_start_time)
        average_delay = total_delay/total_packet_received
        delivery_ratio = total_packet_received/total_packet_sent
        drop_ratio = packet_drop/total_packet_sent
        

        
        #print(f'total Delay:{delay}')
        Throughput.append(throughput)
        Average_delay.append(average_delay)
        Packet_delivery_ratio.append(delivery_ratio)
        Packet_drop_ratio.append(drop_ratio)
        print(f"Throughput:{Throughput[index]}")
        print(f'Average delay:{Average_delay[index]}')
        print(f"packet delivery ratio(parcentage):{Packet_delivery_ratio[index] * 100}")
        print(f"packet drop ratio(percentage): {Packet_drop_ratio[index] * 100}")
        print('\n')

    if t == 0:
        #Graph Plotting
        col = 'green'
        m_col = 'red'
        x_label = 'Area size (m)'
        x_value = area
    elif t == 5:
        col = 'blue'
        m_col = 'red'
        x_label = 'Number of Nodes'
        x_value = nn
    else:
        col = 'yellow'
        m_col = 'red'
        x_label ='Number of Flows'
        x_value = nf

    plt.plot(x_value,Throughput,color=col,marker='o',markerfacecolor=m_col)
    plt.xlabel(x_label)
    plt.ylabel("Throughput(bit/sec)")
    plt.xticks(x_value)
    plt.yticks(Throughput)
    name="plot"+str(t+1)+".png"
    plt.savefig(name,dpi=300,bbox_inches='tight')
    plt.show()

    plt.plot(x_value,Average_delay,color=col,marker='o',markerfacecolor=m_col)
    plt.xlabel(x_label)
    plt.ylabel("Average Delay(sec)")
    plt.xticks(x_value)
    plt.yticks(Average_delay)
    name="plot"+str(t+2)+".png"
    plt.savefig(name,dpi=300,bbox_inches='tight')
    plt.show()

    plt.plot(x_value,Packet_delivery_ratio,color=col,marker='o',markerfacecolor=m_col)
    plt.xlabel(x_label)
    plt.ylabel("Delivery Ratio")
    plt.xticks(x_value)
    plt.yticks(Packet_delivery_ratio)
    name="plot"+str(t+3)+".png"
    plt.savefig(name,dpi=300,bbox_inches='tight')
    plt.show()

    plt.plot(x_value,Packet_drop_ratio,color=col,marker='o',markerfacecolor=m_col)
    plt.xlabel(x_label)
    plt.ylabel("Drop Ratio")
    plt.xticks(x_value)
    plt.yticks(Packet_drop_ratio)
    name="plot"+str(t+4)+".png"
    plt.savefig(name,dpi=300,bbox_inches='tight')
    plt.show()

    Throughput.clear()
    Average_delay.clear()
    Packet_drop_ratio.clear()
    Packet_delivery_ratio.clear()