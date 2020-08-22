#include "Event.h"

Event::Event(EventType type, double time, double length)
{
	this->type = type;
    this->time = time;
    this->length = length;
}

Event::~Event()
{
}
