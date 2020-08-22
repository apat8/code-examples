#include "Simulator.h"
#include "EventScheduler.h"
#include <iostream>
#include <random>

Simulator::Simulator(const double delta, const double tau, const double BER, std::list<double>* results) :
	delta(delta), propogationTime(tau), BER(BER), results(results)
{
}


Simulator::~Simulator()
{
}

std::list<double> Simulator::Start()
{
	Sender();
	(*results).push_back((sucessfullFrameCounter * PACKET_LENGTH) / currentTimeSender);
	Print();
	return *results;
}

void Simulator::Print()
{
	std::cout << "Packets Delivered: " << sucessfullFrameCounter << std::endl;
	std::cout << "Total Time: " << currentTimeSender << std::endl;
	std::cout << "Total Bits Delivered: " << sucessfullFrameCounter * PACKET_LENGTH << std::endl;
	std::cout << "Throughput: " << (sucessfullFrameCounter * PACKET_LENGTH) / currentTimeSender << " bits/sec" << std::endl;
}

void Simulator::Sender()
{
	// Start sim
	EventScheduler* es = new EventScheduler();
	do {			
		// Update currentTimeSender
		currentTimeSender += PACKET_TRANSMISSION_DELAY;
			
		// create timeout event
		double timeOutTime = currentTimeSender + delta;
		es->RegisterEvent(Event::EventType::TIME_OUT, timeOutTime, SN, false);

		// send frame
		Event ackEvent = Send(Frame(currentTimeSender, SN, FRAME_LENGTH));
		currentTimeSender = ackEvent.time;
		if (!ackEvent.lostFlag)
		{
			// add ack to es
			es->RegisterEvent(ackEvent);
		}

		// get next event from es
		Event nextEvent = es->GetEvent();
		currentTimeSender = nextEvent.time; // update time

		// if ACK
		if (nextEvent.type == Event::EventType::ACK)
		{
			// remove timeouts from es
			es->RemoveTimeoutEvents();

			// not corrupted and RN equals nextExpectedACK (successfully delivered packet)
			if (!nextEvent.errorFlag && (nextEvent.sequenceNum == nextExpectedACK))
			{
				// increment counters
				sucessfullFrameCounter++;
				SN = (SN + 1) % 2;
				nextExpectedACK = (nextExpectedACK + 1) % 2;
			}
			else
			{
				continue;
			}
		}
	} while (sucessfullFrameCounter < 5000);
}

Event Simulator::Send(Frame frame)
{
	// Forward Channel
	Frame frameForwardChannel = Channel(frame, frame.length);
	
	Frame frameReceiver = Receiver(frameForwardChannel);
	
	// Reverse Channel
	Frame frameReverseChannel = Channel(frameReceiver, frameReceiver.length);

	return Event(Event::EventType::ACK, frameReverseChannel.time, frameReverseChannel.sequenceNum, frameReverseChannel.errorFlag, frameReverseChannel.lostFrame);
}

Frame Simulator::Channel(Frame frame, int length)
{
	if (!frame.lostFrame)
	{
		int zeroCount = 0;
		for (int i = 0; i < length; i++)
		{
			double randomNumber = (double)rand() / double(RAND_MAX + 1.0);
			if (randomNumber < BER)
			{
				zeroCount++;
			}
		}

		if (zeroCount >= 5)
		{
			// drop packet
			frame.lostFrame = true;
			frame.errorFlag = false;
		}
		else if (zeroCount >= 1 and zeroCount <= 4)
		{
			// error
			// set error flag true
			frame.errorFlag = true;
			frame.lostFrame = false;
		}
		else
		{
			// set error flag false
			frame.errorFlag = false;
			frame.lostFrame = false;
		}

		frame.time += propogationTime;
	}
	return frame;
}

Frame Simulator::Receiver(Frame frame)
{
	currentTimeReceiver = frame.time;
	// check if lost 
	if (!frame.lostFrame)
	{
		// Reciever
		
		if (!frame.errorFlag && frame.sequenceNum == nextExpectedFrame)
		{
			nextExpectedFrame = (nextExpectedFrame + 1) % 2;
			RN = nextExpectedFrame;
		}
		// add transmission time of ACK to transmit ACK
		currentTimeReceiver += ACK_TRANSMISSION_DELAY;
	}
	frame.time = currentTimeReceiver;
	frame.sequenceNum = RN;
	frame.length = HEADER_LENGTH;
	frame.errorFlag = false;
	return frame;
}
