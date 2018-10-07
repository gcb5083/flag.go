from flask import Flask

app = Flask(__name__)

players = {}
flags = [[40.796712][-77.8689215]]
hints = "-The flag is at the one of the most photographed places on campus."

@app.route('/gps', methods=['POST'])
def update_score():
	content = request.json
	passeddata = content.split('\t')
	passeddata[0] = player_id
	passeddata[1] = lat
	passeddata[2] = lng
    	# calculate distance with lat and long, compare to all the flags to find closest flag (distance formula with player lat(x1) long(y1) and flag lat(x2) long(y2))
	for i in range(len(flags)):
		x = lat - flags[i][0]
		y = lng - flag[i][1]
		distance = sqrt(x^2 + y^2)
		if distance <= (20/364567):
			players[player_id] += 1
			flags.remove(flag)
	return jsonify({"score":players[player_id]})

@app.route('/hints', methods=['GET'])
def get_hints():
	return jsonify({"score":hints})
