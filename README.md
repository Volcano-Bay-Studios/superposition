![Featured in BlanketCon '25](https://raw.githubusercontent.com/worldwidepixel/badges/642d312b71811b9d2696b562f735b07288844c71/bc25/featured_in/cozy.svg)
# 🚧 THIS IS A DEMO OF THE MOD 🚧
The mod is not complete and is certinly not meant to be played in survival. Most of the gameplay for the mod is either incomplete or just not added yet.

# What is Superposition?
Superposition in an in development mod that allows the player to manipulate data as they see fit. It has features for gathering information, for transferring it to another point, and for manipulating it into other forms.

Superposition has three main areas of features that each serve their own purpose, some independently from others.
These areas are the following:

📻 **Signal creation and transmission**
💻 **Data encoding and manipulation**
🔢  **Arithmetic and primitive data manipulation**

Here is some of what is in the mod:

## 📻 **Signal creation and transmission**
Signals by themselves are basically just waves that are passed around. There are a number of different features that help in this concept.

### Signal Generator
The signal generator is the starting point for broadcasting a signal. It allows the user to pick a frequency and it will create a signal with that frequency.

🚨The red light in the corner will be lit when the signal that is set is not possible.
![signal generator gui screen](https://file.garden/ZWUae22eyH6XVW5v/signal_generator.png)

### Amplifier
The Amplifier is necessary when broadcasting a signal. Without it the signal is far to weak to be received from another antenna.

🛞 The Amplifier has two dials, one labeled "M" and one labeled "S." The dial labeled "M" is the modulus dial, and will affect the signals amplitude based off of the redstone signal put into the Amplifier from the back. The amplitude of an Amplifier may also be read using a comparator. The dial labeled "S" is the static dial, and will always amplify the signal based off of the value it is set too.

🖥️  The Amplifier has two windows, the one labeled "I" stands for input and shows the signal it is receiving. The window labeled "O" stands for output and shows the signal it is outputting. 

🌡️  The Amplifier has a temperature and it will increase based off of how strong the signals are. You can cool the amplifier using cold blocks to achieve higher amplitudes. We advise that you do not allow the temperature to reach a high level.

⚠️  We advise against amplifying multiple signals simulatinously.
![gif of amplifier screen](https://file.garden/ZWUae22eyH6XVW5v/amplifier_gif.gif)

### Transmitter and Receiver
The Transmitter and Receiver both function in very similar ways, with the minor distinction the one transmits signals and the other receives them.

🎚️ The transmitter takes a redstone signal on the opposite side it receives them, when powered it will broadcast a signal.

📡 The receiver will receive any signal that has the same frequency antenna as it.

🔇 Both the Transmitter and Receiver must have the same (or a factor of) amount of antenna poles. Antennas with the same amount of poles will have no amplitude loss. An antenna with 1/2 length will have a 50% loss, 1/3 a 66%, 1/4  a 75%, etc...

![gif of transmitter/receiver](https://file.garden/ZWUae22eyH6XVW5v/transmitter_gif.gif)

### Cables
Cables are used for transmitting signals short distances. They have a physics simulation that runs while they are moving and being manipulated.

➡️ Cables are monodirectional and move signals from one point to another.

🔁 Cables can be placed on any face of a block regardless of the ports on it.

🔴 The red side is the input side and shows the signals will come from that point.

🔵 The blue side is the output side and shows any signals will be put into that point.

‼️ Cables will stretch to a point and then will be dropped when they are too far from where they are being held.

💡 There is also a glowing varient of the cable that will emit light, the color of which will be set by any signal passing through it that contains a color in the correct format (ex: 0xffffff = white)

![showcase of cable physics](https://file.garden/ZWUae22eyH6XVW5v/cable_gif.gif)

### ⚠️ Everything in the mod is still being worked on and is subject to change.


## TODO
- Make all signal information option data (Similar to an entity component system)
- Make all scattered antenna code unified into one antenna system (for both in real and fake antennas)
- Redo cable collision (Sable in future)
- Inscriber, Cards, and Computers (In progress atm)
- Data corruption with amplitude
- Periphreal Behaviors