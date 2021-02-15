import requests as r
import time
import sys
import os

base_url = 'https://hortinvest.akvolumen.org'
dataset_api_url = base_url + '/api/datasets/{}'
update_api_url = dataset_api_url + '/update'
job_status_url = base_url + '/api/job_executions/dataset/{}'
max_attempts = 120
wait_time = 5

token_url = 'https://akvofoundation.eu.auth0.com/oauth/token'

token_data = {
    'client_id': os.environ['CLIENT_ID'],
    'client_secret': os.environ['CLIENT_SECRET'],
    'username': os.environ['AUTH0_USER'],
    'password': os.environ['AUTH0_PWD'],
    'grant_type': 'password',
    'scope': 'openid email'
}

# Form - dataset mapping
table_dataset = {
    'Business Cases Registration Form NEW': '601bea24-4aed-45fc-b0bd-0a0d7f9c70cf',
    'Event Form': '600ec633-f22e-4751-a96d-16a99c47ca44',
    'Training Attendance registry NEW': '60227d15-1943-42f5-972a-65b29de5864e',
    'Sales Data (After Harvesting and Selling) - NEW - MF to Production': '60227d8f-a538-4b6a-b67f-4da3a469d6e2',
    'Meetings / Workshop Attendance form NEW': '60227ca4-5d8b-4257-9ec3-dde1656e66de',
    'Demo Site Form NEW': '60226db0-1f87-4bc6-8478-a1c534e27905',
    'Farmer Group Form NEW': '60212e22-55a2-426d-bea3-6002e7acb145',
    'Fruit Trees and Seed Distribution Form NEW RF': '60211e1b-d97c-4fab-8414-8a635dbede7c',
    'SME/Company Form NEW': '60224456-7486-4e3b-badc-06d1bb1aebdd',
    'Fruit trees & Seed distribution - MF': '6022a14a-9a9d-4025-8c90-cb72cedcb1a6',
    'Production Data Form (After Planting Period) NEW - RF': '60227d54-e40e-4433-89c7-4d39e7ec7cd4',
    'Cooperative Registration Form NEW': '601c0770-05a4-4281-8fae-b1b959a01b45',
}



def get_token():
    response = r.post(token_url, token_data)
    if response.ok:
        return response.json()['id_token']
    raise RuntimeError('Unable to get access token: HTTP {} - {}'.format(response.status_code, response.text))


def headers(token):
    return {
        'Authorization': 'Bearer ' + token,
        'Host': 'hortinvest.akvolumen.org',
        'Origin': 'https://hortinvest.akvolumen.org',
        'Content-Type': 'application/json',
    }


def wait_for_update(token, job_id):
    for i in range(max_attempts):
        url = job_status_url.format(job_id)
        update_response = r.get(url, headers=headers(token))
        if update_response.ok and update_response.json()['status'] == 'OK':
            print(' - done')
            return True
        print('#', end='')
        time.sleep(wait_time)
    return False


def update_dataset(token, dataset_id):
    print('Updating dataset {}'.format(dataset_id))
    url = update_api_url.format(dataset_id)
    job = r.post(url, headers=headers(token))

    if not job.ok:
        sys.stderr.write('Error updating dataset {} - HTTP {} - '.format(dataset_id, job.status_code, job.text))
        return False

    job_id = job.json()['updateId']

    if not wait_for_update(token, job_id):
        sys.stderr.write('Error updated dataset {}, max attempts reached'.format(dataset_id))
        return False

    return True



if __name__ == '__main__':
    prefix = int(time.time())

    for d in table_dataset:
        print('Processing: ' + d)
        dataset_id = table_dataset[d]
        token = get_token()
        update_dataset(token, dataset_id)
