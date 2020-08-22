#include "Simulation.h"
#include "EventScheduler.h"

Simulation::Simulation(double simulationTime, double rho, double averagePacketLength, double linkRate, double bufferSize)
{
	this->simulationTime = simulationTime;
	this->rho = rho;
	this->averagePacketLength = averagePacketLength;
	this->linkRate = linkRate;
	this->bufferSize = bufferSize;

	this->lambda = rho * linkRate / averagePacketLength;
	this->alpha = lambda * 4;

	this->furthestDepartureTime = 0;
	this->currentTime = 0; 

	// Initialize counters to zero
	this->numOfPacketsArrival = 0;
	this->numOfPacketsDeparture = 0;
	this->numOfObservations = 0;
	this->numOfDroppedPackets = 0;
	this->numOfGeneratedPackets = 0;
	this->idleCounter = 0;

	this->avgPacketsInBuffer = 0;
	this->sumPacketsInBuffer = 0;
	this->pIdle = 0;
	this->pLoss = 0;
}


Simulation::~Simulation()
{
}

void Simulation::Start()
{
	// Populate event scheduler and get event list
	EventScheduler es(simulationTime, averagePacketLength, lambda, alpha);
	this->eventsList = &es.eventsList;
	
	

	// Dequeue each packet and process it
	while (!(this->eventsList->empty())) {
		// get packet and remove from list
		Event packetBeingProcessed = this->eventsList->top();
		eventsList->pop();
		
		// update current time in simulation
		currentTime = packetBeingProcessed.time;
		
        // processEvent
		processEvent(packetBeingProcessed);
	}
	
}

// Process events from buffer
void Simulation::processEvent(Event packet)
{
	if (packet.type == Event::EventType::Arrival)
	{
		// increment number of packets generated 
        this->numOfGeneratedPackets++;
		
        if(isBufferFull())
        {
            // Increment packet lost counter
			this->numOfDroppedPackets++;
        }
        else
        {
            // Increment packet arrival counter
            this->numOfPacketsArrival++;

			// calculate departure time (arrival time + service time +  buffer wait)
			// furthestDepartureTime = arrival time + buffer wait
			double serviceTime = packet.length / this->linkRate;
			double departureTime = 0;
            if(!isServerBusy())
            {
                departureTime = packet.time + serviceTime;
            }
            else
            {
                departureTime = this->furthestDepartureTime + serviceTime;
            }
            
			this->furthestDepartureTime = departureTime;

			if (departureTime <= this->simulationTime)
			{
				// create departure event
				Event departureEvent(Event::EventType::Departure, departureTime, packet.length);
                this->eventsList->push(departureEvent);
			}
        }
	}
	else if (packet.type == Event::EventType::Departure)
	{
		// Increment departure packets counter
        this->numOfPacketsDeparture++;
	}
	else
	{
		// increment number of observer
		this->numOfObservations++;
		
        // if server is empty (buffer is also empty) increment idle counter
        if (isSystemEmpty())
		{
			this->idleCounter++;
		} 

        // Get number of packets in system for every observer and
        // keep a running sum for this simulation
		double packetsInSystem = PacketsInSystem();
		this->sumPacketsInBuffer += packetsInSystem; 

		// E[n] average number of packets in the system
		this->avgPacketsInBuffer = (double)sumPacketsInBuffer / (double)numOfObservations;

		//Pidle - proportion of time server is idle
		this->pIdle = (double)idleCounter / (double)numOfObservations;

		//ploss - packet loss probability
		this->pLoss = (double)numOfDroppedPackets / (double)numOfGeneratedPackets;
	}
	
}

// Check if server is busy (processing packet)
bool Simulation::isServerBusy()
{
	return (this->furthestDepartureTime > this->currentTime);
}

// Check if buffer is full
// If buffer size is -1, buffer size is infinite
bool Simulation::isBufferFull()
{
	if (bufferSize == -1)
	{
		return false;
	}
	return (PacketsInSystem()) > bufferSize;
}

// Number of packets in the system
int Simulation::PacketsInSystem()
{
	return (this->numOfPacketsArrival - this->numOfPacketsDeparture);
}

// Check if system has no packets
bool Simulation::isSystemEmpty()
{
	return (PacketsInSystem() == 0);
}