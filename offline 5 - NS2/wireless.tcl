#for {set num 14} { $num < 15 } { incr num } {
    
set num [lindex $argv 0]
# simulator
set ns [new Simulator]
puts "simulator"
puts $num

# ======================================================================
# Define options

set val(chan)         Channel/WirelessChannel  ;# channel type
set val(prop)         Propagation/TwoRayGround ;# radio-propagation model
set val(ant)          Antenna/OmniAntenna      ;# Antenna type
set val(ll)           LL                       ;# Link layer type
set val(ifq)          CMUPriQueue   ;# Interface queue type
set val(ifqlen)       50                       ;# max packet in ifq
set val(netif)        Phy/WirelessPhy/802_15_4          ;# network interface type
set val(mac)          Mac/802_15_4             ;# MAC type
set val(rp)           DSR                     ;# ad-hoc routing protocol 
if {$num < 5} {
set val(x)            [expr (250*($num+1))]                       ;# length of area    
} else {
set val(x)            500                      ;# length of area
}
if {$num > 4 && $num < 10 } {
set val(nn)           [expr ($num-4)*20]                       ;# number of mobilenodes
} else {
set val(nn)           40                       ;# number of mobilenodes
}
set val(y)            $val(x)                       ; #width of area
set stop_time         50.0001                   ; 
if {$num > 9 && $num < 15} {
set val(nf)           [expr ($num-9)*10]                          ;# number of flows
} else {
set val(nf)           20                          ;# number of flows
}
# =======================================================================
puts "Area"
puts $val(x)
puts "Number of nodes"
puts $val(nn)
puts "Number of flows"
puts $val(nf)
# trace file
set trace_file_name "trace"
append trace_file_name $num
append trace_file_name ".tr"
set trace_file [open $trace_file_name w]
$ns trace-all $trace_file
puts $trace_file_name
# nam file
set nam_file [open animation.nam w]
$ns namtrace-all-wireless $nam_file $val(x) $val(y)

# topology: to keep track of node movements
set topo [new Topography]
$topo load_flatgrid $val(x) $val(y) ;# 500m x 500m area


# general operation director for mobilenodes
create-god $val(nn)

# node configs
# ======================================================================

# $ns node-config -addressingType flat or hierarchical or expanded
#                  -adhocRouting   DSDV or DSR or TORA
#                  -llType	   LL
#                  -macType	   Mac/802_11
#                  -propType	   "Propagation/TwoRayGround"
#                  -ifqType	   "Queue/DropTail/PriQueue"
#                  -ifqLen	   50
#                  -phyType	   "Phy/WirelessPhy"
#                  -antType	   "Antenna/OmniAntenna"
#                  -channelType    "Channel/WirelessChannel"
#                  -topoInstance   $topo
#                  -energyModel    "EnergyModel"
#                  -initialEnergy  (in Joules)
#                  -rxPower        (in W)
#                  -txPower        (in W)
#                  -agentTrace     ON or OFF
#                  -routerTrace    ON or OFF
#                  -macTrace       ON or OFF
#                  -movementTrace  ON or OFF

# ======================================================================

$ns node-config -adhocRouting $val(rp) \
                -llType $val(ll) \
                -macType $val(mac) \
                -ifqType $val(ifq) \
                -ifqLen $val(ifqlen) \
                -antType $val(ant) \
                -propType $val(prop) \
                -phyType $val(netif) \
                -topoInstance $topo \
                -channelType $val(chan) \
                -agentTrace ON \
                -routerTrace ON \
                -macTrace OFF \
                -movementTrace OFF


# create nodes
for {set i 0} {$i < $val(nn) } {incr i} {
    set node($i) [$ns node]
    set speed [expr rand() * (5)]
    set speed [expr round($speed)]
    
    if {$speed==0} {
        set speed 1
        
    }
    #puts $speed
    set a [expr round([expr rand() * ($val(x)-1)])]
    #puts $a
    #$node($i) random-motion 0       ;# disable random motion
    
    $node($i) set X_ [expr rand() * ($val(x)-1)]
    $node($i) set Y_ [expr rand() * ($val(y)-1)]
    $node($i) set Z_ 0
    set des(x) [expr round([expr rand() * ($val(x)-10)])]
    set des(y) [expr round([expr rand() * ($val(y)-10)])]
    set time [expr round([expr rand() * (10)])]
    set time [expr $time+3]
    if {$des(x) < 1} {
        set des(x) 10
    }
    if {$des(y) < 1} {
        set des(y) 10
    }
    $ns at $time "$node($i) setdest $des(x) $des(y) $speed"
    $node($i) start
    $node($i) start
    $ns initial_node_pos $node($i) 10
} 
#puts "Check"


# Traffic
#sink selection
set sink [expr rand() * ($val(nn)-1)]
puts "Sink:"
set sink [expr round($sink)]
puts $sink
set tcp_sink [new Agent/TCPSink]
$ns attach-agent $node($sink) $tcp_sink
for { set index 0 }  { $index < $val(nn) }  { incr index } {
   set arr($index) 0
   
}


# create flow

for {set i 0} {$i < $val(nf)} {incr i} {

    set j1 [expr rand() * ($val(nn)-1)]
    set j [expr round($j1)]
    if {$j == $sink } {
        set j [expr ($j+1)]
   
    } else {
        set arr($j) 1
        
    }

    #puts $i
    set src $j
    #set dest $sink

    # Traffic config
    # create agent
    set tcp [new Agent/TCP]
    #set tcp_sink [new Agent/TCPSink]
    # attach to nodes
    $ns attach-agent $node($src) $tcp
    #$ns attach-agent $node($dest) $tcp_sink
    # connect agents
    $ns connect $tcp $tcp_sink
    $tcp set fid_ $i

    # Traffic generator
    set ftp [new Application/Telnet]
    #$p($i) set packetSize_ 1500
    # attach to agent
    $ftp attach-agent $tcp
    $ftp set packetSize_ 100
    #$ftp set interval_ 5
    
    # start traffic generation
    $ns at 1.0 "$ftp start"
}



# End Simulation

# Stop nodes
for {set i 0} {$i < $val(nn)} {incr i} {
    $ns at [expr ($stop_time-0.01)] "$node($i) reset"
}

# call final function
proc finish {} {
    global ns trace_file nam_file
    $ns flush-trace
    close $trace_file
    close $nam_file
}

proc halt_simulation {} {
    global ns
    puts "Simulation ending"
    $ns halt
}

$ns at $stop_time "finish"
$ns at [expr ($stop_time+.01)] "halt_simulation"




# Run simulation
puts "Simulation starting"
$ns run
#}
