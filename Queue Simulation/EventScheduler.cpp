#include "EventScheduler.h"
#include <iostream>

EventScheduler::EventScheduler(double totalRunTime, double packetLength, double lambda, double alpha)
{
	this->totalRunTime = totalRunTime;
	this->packetLength = packetLength;
	this->lambda = lambda;
	this->alpha = alpha;
    
    GenerateArrivalEvents();
    GenerateObserverEvents();
}

EventScheduler::~EventScheduler()
{
}

// Generate events and add to events list
void EventScheduler::GenerateEvents(Event::EventType eventType, double randomNumberParameter)
{
    double currentTime = 0;
	
    // generate event as long as current time is less than simulation time
	while (currentTime < (this->totalRunTime))
	{
		// generate time 
		double eventTime = GenerateExponentialRandomNumber(randomNumberParameter);
		
        // increment current time
		currentTime += eventTime;

		// create new event and add to list
        if(eventType == Event::EventType::Arrival)
        {
            Event newEvent(Event::EventType::Arrival, currentTime, GenerateExponentialRandomNumber(1 / (this->packetLength)));
            this->eventsList.push(newEvent);            
        }
        else
        {
            Event newEvent(Event::EventType::Observer, currentTime, 0);
            this->eventsList.push(newEvent);
        }
	} 
    
}

// generate arrival events
void EventScheduler::GenerateArrivalEvents()
{
	GenerateEvents(Event::EventType::Arrival, this->lambda);
}

// Generate observer events
void EventScheduler::GenerateObserverEvents()
{
	GenerateEvents(Event::EventType::Observer, this->alpha);
}

// Generate random number
double EventScheduler::GenerateExponentialRandomNumber(double parameter)
{
	double uniformRandomVariable = (double)rand() / double(RAND_MAX + 1.0);
	return (log(1.0 - uniformRandomVariable) / (-1.0 * parameter));
}

// Question 1
void EventScheduler::DisplayRandomNumbers()
{
	double sumMean = 0;

	for (int i = 0; i < 1000; i++)
	{
		double s = GenerateExponentialRandomNumber(75);
		sumMean += s;

		std::cout << "Rand Num: " << s << std::endl;
	}
	double mean = sumMean / 1000;
	std::cout << "Mean = " << mean << std::endl;
}