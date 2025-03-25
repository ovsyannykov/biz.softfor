call mvn clean vaadin:clean-frontend
del /f/s/q package-lock.json package.json tsconfig.json types.d.ts vite.config.ts vite.generated.ts
call mvn -DskipTests install
call mvn vaadin:build-frontend
