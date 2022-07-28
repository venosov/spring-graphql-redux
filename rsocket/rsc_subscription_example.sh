rsc --stream --route=graphql --dataMimeType="application/graphql+json" --data='{"query":"subscription { greetings {greeting}}" }' --debug tcp://localhost:9191
