# Teams Prototype Docs

If I ever merge this, I'll probably forget to remove this, oops

This is documentation for the API endpoints I made for the Chusan Teams V2 prototype.

## View Actions

### Team List
`api/v2/game/chu3/teams`<br>
Can include `token`, not necessary

```json
{
  "teams": [
    {
      "id": 7,
      "rank": 1,
      "name": "Rotten Girl",
      "memberCount": 1,
      "points": 100 // Total points
    }
  ],
  "team": null, // This will be your team ID, if you're in one
  "createStatus": {
    "minimum": 10, // Minimum Chusan level to create a team
    "qualified": false // If you meet requirements to create a team
  }
}
```

### Detailed Team View
`api/v2/game/chu3/team`<br>
Must include `id`, team ID, no authentication necessary

```json
{
  "ranking": 1,
  "id": 7,
  "points": 100, // Total points
  "members": [
    {
      "contribution": 100,
      "username": "judyhopps",
      "name": "JudyHopp"
    }
  ],
  "isFull": false, // Whether or not you're able to join
  "owner": {
    "username": "judyhopps",
    "displayName": "Judy Hopps"
  }
}
```

### Team Requests
`api/v2/game/chu3/team-requests`<br>
Must include authentication

```json
{
  "incoming": [
    // All incoming requests, if you are a team manager / owner (there can be multiple)
    {
      "id": 6,
      "date": "2025-06-20T04:27:18.861088",
      "user": {
        "username": "nickwilde",
        "displayName": ""
      }
    }
  ],
  "outgoing": null // Outgoing requests
}
```

## Team Management Actions

### Create a Team
`api/v2/game/chu3/create-team`<br>
Must include authentication, `teamName` team name
```json
{"status":"ok"}
```
... or a plaintext error

### Manage a join request
`api/v2/game/chu3/team-request`<br>
Must include authentication, `requestId` request's id, `status` boolean for accept or deny
```json
{"status":"ok"}
```
... or a plaintext error

### Join, leave or delete a team.

> :warning: This endpoint CAN be destructive.<br>
> If you are managing a team, it WILL delete your team.<br>
> Ensure you add, on your interface, a confirmation prompt when using this endpoint.

`api/v2/game/chu3/team-join`<br>
Must include authentication.<br>
Set `teamId` to the team you'd like to join, OR leave it empty to leave / **destroy** your team.<br>
*In the future, this should only leave your team, only abandoning it if you manage one instead of destroying*

```json
{"status":"ok"}
```
... or a plaintext error