# This Dockerfile is used for debugging purposes to test how the App behaves when containerized. Run it under Linux docker.

FROM mcr.microsoft.com/dotnet/aspnet:6.0-alpine AS base

WORKDIR /app
EXPOSE 5000

FROM mcr.microsoft.com/dotnet/sdk:6.0-alpine AS build
WORKDIR /src
COPY ["Rws.LC.AppBlueprint/Rws.LC.AppBlueprint.csproj", "Rws.LC.AppBlueprint/"]
RUN dotnet restore "Rws.LC.AppBlueprint/Rws.LC.AppBlueprint.csproj"
COPY . .
WORKDIR "/src/Rws.LC.AppBlueprint"
RUN dotnet build "Rws.LC.AppBlueprint.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "Rws.LC.AppBlueprint.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "Rws.LC.AppBlueprint.dll"]