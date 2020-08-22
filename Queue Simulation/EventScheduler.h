#pragma once
#include <queue>
#include "Event.h"
#include <math.h>


struct Comp {
	bool operator()(const Event& event1, const Event& event2)
	{
		return event1.time > event2.time;
	}
};

class EventScheduler
{
public:
	EventScheduler(double totalRunTime, double packetLength, double lambda, double alpha);
	~EventScheduler();

	// member variables
	double totalRunTime;
	double packetLength;
	double lambda;
	double alpha;
	std::priority_queue<Event, std::vector<Event>, Comp> eventsList;

	// generate events
    void GenerateEvents(Event::EventType eventType, double randomNumberParameter);
	void GenerateArrivalEvents();
	void GenerateObserverEvents();

	// generate arrival/observer times
	double GenerateExponentialRandomNumber(double parameter);
	
	// Displaying purposes
	void DisplayRandomNumbers();
};

