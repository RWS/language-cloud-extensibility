# This Dockerfile is used for debugging purposes to test how the App behaves when containerized. Run it under Linux docker.

FROM mcr.microsoft.com/dotnet/aspnet:8.0-alpine AS base

WORKDIR /app
EXPOSE 5000

FROM mcr.microsoft.com/dotnet/sdk:8.0-alpine AS build
WORKDIR /src
COPY ["Rws.LC.MTSampleApp/Rws.LC.MTSampleApp.csproj", "Rws.LC.MTSampleApp/"]
RUN dotnet restore "Rws.LC.MTSampleApp/Rws.LC.MTSampleApp.csproj"
COPY . .
WORKDIR "/src/Rws.LC.MTSampleApp"
RUN dotnet build "Rws.LC.MTSampleApp.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "Rws.LC.MTSampleApp.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "Rws.LC.MTSampleApp.dll"]