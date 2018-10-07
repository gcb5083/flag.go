from flask import Flask

app = Flask(__name__)

@app.route('/gps', methods=['POST'])
    content = request.json          #TODO
    score = update_score(content[0], conent[1], content[2])
    return jsonify({"score":score})

@app.route('/hints', methods=['GET'])
    hints = get_hints()
    return jsonify({"score":hints})
    
players = {}
flags = [][]
hints = ""

# increases player's score, removes flag (delete from flag list); returns player's score
def update_score(player_id, lat, lng):
    # calculate distance with lat and long, compare to all the flags to find closest flag (distance formula with player lat(x1) long(y1) and flag lat(x2) long(y2))
    for i in range(len(flags)):
        x = lat - flags[i][0]
        y = lng - flag[i][1]
        distance = sqrt(x^2 + y^2)
        if distance <= (20/364567):
            players[player_id] += 1
            flags.remove(flag)
    return players[player_id]

def get_hints():
    return hints
