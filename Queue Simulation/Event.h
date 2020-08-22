class Event
{
public:
	enum EventType { Arrival, Departure, Observer };

	EventType type;
	double time;
	double length;
	
	Event(EventType type, double time, double length);
	~Event();

	
};

