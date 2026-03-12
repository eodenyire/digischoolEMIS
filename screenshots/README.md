# Screenshots

This folder contains screenshots of the DigiSchool EMIS system as it runs end-to-end.

Add screenshots here to document the running state of the application — including UI views, API responses, and any other relevant visual artefacts captured during testing or demonstrations.

## Naming Convention

Use descriptive, lowercase, hyphen-separated file names so screenshots are easy to navigate:

```
<module>-<feature>-<description>.png
```

Examples:

| File | Description |
|------|-------------|
| `auth-login-success.png` | Successful login response from the Auth endpoint |
| `students-create-student.png` | Create-student API response |
| `health-endpoint-ok.png` | `/health` endpoint returning `{"status": "ok"}` |

## End-to-End Test Results

All Wave 1 API stub tests pass:

```
tests/test_api.py::test_health_endpoint          PASSED
tests/test_api.py::test_auth_login_endpoint      PASSED
tests/test_api.py::test_create_student_endpoint  PASSED

3 passed in 0.49 s
```

Run the tests yourself:

```bash
cd services/wave1-fastapi
pip install -r requirements.txt
python -m pytest tests/ -v
```
