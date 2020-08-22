#include <iostream>
#include <stdlib.h>
#include <math.h>
#include <time.h> // REMOVE: for compling in visual studio
#include "Simulation.h"
#include <iomanip>

// Generate random number
double GenerateExponentialRandomNumber()
{
	double uniformRandomVariable = (double)rand() / double(RAND_MAX + 1.0);
	return (double)(log(1 - uniformRandomVariable)) / -75;
}

void DisplayRandomNumbers()
{
	double sumMean = 0;

	for (int i = 0; i < 1000; i++)
	{
		double s = GenerateExponentialRandomNumber();
		sumMean += s;

		std::cout << "Rand: " << s << std::endl;
	}
	double mean = sumMean / 100;
	std::cout << "Mean = " << mean << std::endl;
}

int main()
{
	srand(time(NULL));

	//// Input Question 2
	int simulationLength = 10000;
	int avgPacktLength = 12000;
	int linkRate = 1000000;
	double bufferSize = -1;
	double rhoUpperBound = 0.95;
	double rhoLowerBound = 0.25;
	double rhoStep = 0.1;
    
    std::cout << "rho" << std::left << std::setw(20) << "E[n}" << std::left << std::setw(20) << "Pidle" << std::left << std::setw(20) << "Ploss" << std::left << std::setw(20) << "arrivals" << std::left << std::setw(20) << "departures\n";
    // Start with rhoUpper, incremnt by 0.1 and call simulator
	// run simulation with current rho
	double currentRho = rhoLowerBound;
	while (currentRho < (rhoUpperBound + rhoStep))
	{
		Simulation* simulator = new Simulation(simulationLength, currentRho, avgPacktLength, linkRate, bufferSize);
		simulator->Start();
        
       
		std::cout << std::setw(20) << currentRho  << std::setw(20) << simulator->avgPacketsInBuffer  << std::setw(20) << simulator->pIdle  << std::setw(20) << simulator->pLoss  << std::setw(20) << simulator->numOfPacketsArrival  << std::setw(20) << simulator->numOfPacketsDeparture << "\n"; 
		currentRho += rhoStep;
	}		
}






