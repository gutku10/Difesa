#include <ESP8266WiFi.h>
#define PubNub_BASE_CLIENT WiFiClient
#include <PubNub.h>
static char ssid[] = "LC";
static char pass[] = "lc@tiet1";
const static char pubkey[]  = "pub-c-7de5c2f2-52b1-4486-b8ce-0f950f35e57c";
const static char subkey[]  = "sub-c-9301af48-556e-11ea-b828-26d2a984a2e5";
const static char channel[] = "Help_Me";
String message;
int val = 0;
int LED = D5; // choose the pin for the LED
int inPin = D7;   // choose the input pin (for a pushbutton)

void setup() {

  pinMode(LED, OUTPUT);
  pinMode(inPin, INPUT);

  Serial.begin(9600);
  Serial.println("Attempting to connect...");
  WiFi.begin(ssid, pass);
  if (WiFi.waitForConnectResult() != WL_CONNECTED) { // Connect to WiFi.
    Serial.println("Couldn't connect to WiFi.");
    while (1) delay(100);
  }
  else {
    Serial.print("Connected to SSID: ");
    Serial.println(ssid);
    PubNub.begin(pubkey, subkey); // Start PubNub.
    Serial.println("PubNub is set up.");
  }
}
void loop() {
  val = digitalRead(inPin);  // read input value
  if (val == HIGH) {         // check if the input is HIGH (button released)
    digitalWrite(LED, LOW);  // turn LED OFF
  } else {
    digitalWrite(LED, HIGH);  // turn LED ON
    Serial.println("HIGH");
    subscribe();
    publish();
  }
}
void subscribe()
{ // Subscribe.
  PubSubClient* sclient = PubNub.subscribe(channel); // Subscribe.
  if (0 == sclient) {
    Serial.println("Error subscribing to channel.");
    return;
  }
  while (sclient->wait_for_data()) { // Print messages.
    Serial.write(sclient->read());
  }
  sclient->stop();
}
void publish()
{ // Publish.
  char msg[] = "\"Abhinav\"";
  WiFiClient* client = PubNub.publish(channel, msg); // Publish message.
  if (0 == client) {
    Serial.println("Error publishing message.");
    return;
  }
  else {
    Serial.println("Sent");
  }
  client->stop();
}
