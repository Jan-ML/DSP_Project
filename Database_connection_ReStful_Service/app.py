import json
from flask import Flask, jsonify, request
from flask.json import JSONEncoder
from flask_sqlalchemy import SQLAlchemy
import psycopg2
from datetime import date
from sqlalchemy import and_


class CustomJSONEncoder(JSONEncoder):
    def default(self, obj):
        try:
            if isinstance(obj, date):
                return obj.isoformat()
            iterable = iter(obj)
        except TypeError:
            pass
        else:
            return list(iterable)
        return JSONEncoder.default(self, obj)


app = Flask(__name__)
app.json_encoder = CustomJSONEncoder
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config[
    'SQLALCHEMY_DATABASE_URI'] = 'postgresql://Jan_17026846:Jasiu995@uwe-azure.postgres.database.azure.com/lstm_data'
db = SQLAlchemy(app)


class LSTM(db.Model):
    __tablename__ = 'Feed'
    Month = db.Column(db.Date(), nullable=False)
    Total_Crimes = db.Column(db.Integer(), nullable=False)
    feed_id = db.Column(db.Integer(), primary_key=True, nullable=False)

    def __init__(self, Month, Total_Crimes, feed_id):
        self.Month = Month
        self.Total_Crimes = Total_Crimes
        self.feed_id = feed_id


class TrainingData(db.Model):
    __tablename__ = 'LSTM_Training_Data'
    Crime_ID = db.Column(db.Integer(), primary_key=True, nullable=False)
    Month = db.Column(db.Date(), nullable=False)
    Longitude = db.Column(db.Float(), nullable=False)
    Latitude = db.Column(db.Float(), nullable=False)
    Crime_type = db.Column(db.Text(), nullable=False)
    OCPM = db.Column(db.Integer(), nullable=False)
    Neighbourhood = db.Column(db.Text(), nullable=False)
    Reference_number = db.Column(db.Integer(), nullable=False)

    def __init__(self, Crime_ID, Month, Longitude, Latitude, Crime_type, OCPM, Neighbourhood, Reference_number):
        self.Crime_ID = Crime_ID
        self.Month = Month
        self.Longitude = Longitude
        self.Latitude = Latitude
        self.Crime_type = Crime_type
        self.OCPM = OCPM
        self.Neighbourhood = Neighbourhood
        self.Reference_number = Reference_number


class PoliceDB(db.Model):
    __tablename__ = 'Police_DB'
    ID = db.Column(db.Integer(), primary_key=True, nullable=False)
    Month = db.Column(db.Date(), nullable=False)
    Crime_type = db.Column(db.Text(), nullable=False)
    Neighbourhood = db.Column(db.Text(), nullable=False)
    Reference_number = db.Column(db.Integer(), nullable=False)

    def __init__(self, Crime_ID, Month,Crime_type,Neighbourhood, Reference_number):
        self.ID = Crime_ID
        self.Month = Month
        self.Crime_type = Crime_type
        self.Neighbourhood = Neighbourhood
        self.Reference_number = Reference_number


@app.route('/feed', methods=['GET'])
def get_feed():
    all_feeds = LSTM.query.all()
    output = {"feed": []}
    for feed in all_feeds:
        current_feed = {'Month': feed.Month, 'Total_Crimes': str(feed.Total_Crimes)}
        output['feed'].append(current_feed)
    return jsonify(output)


@app.route('/feed', methods=['POST'])
def post_feed():
    feed_data = json.loads(request.data)
    crime_type = feed_data['crime']['Crime_type']
    crime_district = feed_data['crime']['District']
    crime_date = feed_data['crime']['Year']
    output = {"crime": {"Date": [],
                        "District": [],
                        "Crime_type": [],
                        "Crime_number": []}}

    if crime_type == "All" and crime_district == "All":
        for i in range(1, 13):
            extracted_date = get_date(crime_date, i)
            crime = find_all_bristol(extracted_date)
            find_crime(output, crime)
    elif crime_type == "All":
        for i in range(1, 13):
            extracted_date = get_date(crime_date, i)
            crime = find_specific_crime_type(extracted_date, crime_district)
            find_crime(output, crime)
    elif crime_district == "All":
        for i in range(1, 13):
            extracted_date = get_date(crime_date, i)
            crime = find_specific_district(extracted_date, crime_type)
            find_crime(output, crime)
    else:
        for i in range(1, 13):
            extracted_date = get_date(crime_date, i)
            crime = find_specific_crime(extracted_date, crime_type, crime_district)
            find_crime(output, crime)
    clear_output(output)
    return jsonify(output)


def get_date(year, month):
    if month < 10:
        year = year + "-" + "0" + str(month) + "-01"
    else:
        year = year + "-" + str(month) + "-01"
    return year


def find_crime(output, crime):
    total_crimes = 0
    for c in crime:
        output['crime']['Date'].append(c.Month.strftime("%Y-%m"))
        output['crime']['District'].append(c.Neighbourhood)
        output['crime']['Crime_type'].append(c.Crime_type)
        total_crimes += 1
    if total_crimes is not 0:
        output['crime']['Crime_number'].append(total_crimes)


def find_specific_crime(extracted_date, crime_type, crime_district):
    crime = TrainingData.query.filter(
        and_(TrainingData.Month == extracted_date, TrainingData.Crime_type == crime_type,
             TrainingData.Neighbourhood == crime_district))
    return crime


def find_specific_district(extracted_date, crime_type):
    crime = TrainingData.query.filter(
        and_(TrainingData.Month == extracted_date, TrainingData.Crime_type == crime_type))
    return crime


def find_specific_crime_type(extracted_date, crime_district):
    crime = TrainingData.query.filter(
        and_(TrainingData.Month == extracted_date, TrainingData.Neighbourhood == crime_district))
    return crime


def find_all_bristol(extracted_date):
    crime = TrainingData.query.filter(
        and_(TrainingData.Month == extracted_date))
    return crime


def clear_output(output):
    output['crime']['Date'] = list(dict.fromkeys(output['crime']['Date']))
    output['crime']['District'] = list(dict.fromkeys(output['crime']['District']))
    output['crime']['Crime_type'] = list(dict.fromkeys(output['crime']['Crime_type']))


@app.route('/report', methods=['GET'])
def get_report():
    all_feeds = PoliceDB.query.all()
    output = {"report": []}
    for feed in all_feeds:
        current_feed = {'Date': feed.Month, 'Crime_type': feed.Crime_type, 'District': feed.Neighbourhood}
        output['report'].append(current_feed)
    return jsonify(output)


@app.route('/report', methods=['POST'])
def post_report():
    feed_data = json.loads(request.data)
    crime_type = feed_data['report']['Crime_type']
    crime_district = feed_data['report']['District']
    crime_date = feed_data['report']['Date']
    crime_date = crime_date + "-01"
    crime_reference_number = feed_data['report']['Reference_number']
    report = PoliceDB.query.all()
    output = {"report": {
        "Ref_number": "None"
    }}
    for reported_crime in report:
        if reported_crime.Reference_number == crime_reference_number:
            reported_crime.Month = crime_date
            reported_crime.Crime_type = crime_type
            reported_crime.Neighbourhood = crime_district
            db.session.commit()
            output['report']['Ref_number'] = "True"
    return jsonify(output)


if __name__ == '__main__':
    app.run()
