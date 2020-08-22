#include "EventScheduler.h"

class Simulation
{
public:

    // Member variables
	double simulationTime;
	double averagePacketLength;
	double linkRate;
	double rho;
	double bufferSize;
	double lambda;
	double alpha;
    std::priority_queue<Event, std::vector<Event>, Comp>* eventsList;
	double currentTime;
	double furthestDepartureTime;

	//Counters
	int numOfPacketsArrival;
	int numOfPacketsDeparture;
	int numOfObservations;
	int numOfDroppedPackets;
	int numOfGeneratedPackets;
	int idleCounter;

    // Measurements
	double avgPacketsInBuffer;
	double sumPacketsInBuffer;
	double pIdle;
	double pLoss;

	Simulation(double simulationTime, double rho, double averagePacketLength, double linkRate, double bufferSize);
	~Simulation();

	void Start();
	void processEvent(Event packet);
	bool isServerBusy();
	bool isBufferFull();
	int PacketsInSystem();
	bool isSystemEmpty();
};

