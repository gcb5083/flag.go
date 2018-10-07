from flask import Flask, jsonify, request
import math

app = Flask(__name__)

players = {}
flags = [[40.796712, -77.8689215]]

@app.route('/playerid', methods=['POST'])
def get_playerID():
	content = request.json
	players[content["playerID"].split('|')[0]] = 0

@app.route('/gps', methods=['POST'])
def update_score():
	content = request.json
	print(content)
	passeddata = content["location"].split('|')
	player_id = passeddata[0]
	lat = passeddata[1]
	lng = passeddata[2]
    	# calculate distance with lat and long, compare to all the flags to find closest flag (distance formula with player lat(x1) long(y1) and flag lat(x2) long(y2))
	for i in range(len(flags)):
		x = float(lat) - flags[i][0]
		y = float(lng) - flags[i][1]
		distance = math.sqrt(x**2 + y**2)
		print(700/364567)
		print(distance)
		if distance <= (700/364567):
			players[player_id] += 1
			del flags[i]
	print(players[player_id])
	return jsonify({"score":("---" + str(players[player_id]) + "---")})

@app.route('/hints')
def get_hints():
	return jsonify({"hint":"---The flag is at one of the most photographed places on campus.---"})


