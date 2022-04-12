# This Dockerfile is used for debugging purposes to test how the Add-On behaves when containerized. Run it under Linux docker.

FROM mcr.microsoft.com/dotnet/aspnet:6.0-alpine AS base

WORKDIR /app
EXPOSE 5000

FROM mcr.microsoft.com/dotnet/sdk:6.0-alpine AS build
WORKDIR /src
COPY ["Rws.LC.AddonBlueprint/Rws.LC.AddonBlueprint.csproj", "Rws.LC.AddonBlueprint/"]
RUN dotnet restore "Rws.LC.AddonBlueprint/Rws.LC.AddonBlueprint.csproj"
COPY . .
WORKDIR "/src/Rws.LC.AddonBlueprint"
RUN dotnet build "Rws.LC.AddonBlueprint.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "Rws.LC.AddonBlueprint.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "Rws.LC.AddonBlueprint.dll"]