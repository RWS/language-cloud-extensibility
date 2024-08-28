# This Dockerfile is used for debugging purposes to test how the App behaves when containerized. Run it under Linux docker.

FROM mcr.microsoft.com/dotnet/aspnet:8.0-alpine AS base

RUN apk add icu-libs

WORKDIR /app
EXPOSE 5000

FROM mcr.microsoft.com/dotnet/sdk:8.0-alpine AS build
WORKDIR /src
COPY ["Rws.LC.VerificationSampleApp/Rws.LC.VerificationSampleApp.csproj", "Rws.LC.VerificationSampleApp/"]
RUN dotnet restore "Rws.LC.VerificationSampleApp/Rws.LC.VerificationSampleApp.csproj"
COPY . .
WORKDIR "/src/Rws.LC.VerificationSampleApp"
RUN dotnet build "Rws.LC.VerificationSampleApp.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "Rws.LC.VerificationSampleApp.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "Rws.LC.VerificationSampleApp.dll"]