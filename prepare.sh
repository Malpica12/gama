#!/bin/bash

MESSAGE=$(git log -1 HEAD --pretty=format:%s)
echo $MESSAGE
if  [[ ${MESSAGE} == *"ci deploy"* ]]; then		
		if  [[ ${MESSAGE} == *"ci clean"* ]]; then
				echo "Cleaning p2 update site"		
				sshpass -e ssh gamaws@51.255.46.42 /var/www/gama_updates/clean.sh
		fi
        echo "Deploy to p2 update site"		
		sh ./publish.sh
else
        echo "Build GAMA project"		
		sh ./build.sh
fi

if  [[ ${MESSAGE} == *"ci release"* ]]; then		
		bash ./github-release.sh "$TRAVIS_COMMIT" 
fi
echo "do nothing"		