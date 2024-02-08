{{/*
Generate configmap name suffix based on release number
*/}}
{{- define "zio-http-poc-chart.cm-suffix" -}}
{{- .Chart.Version | default "1.0.0" | replace "." "-" }}
{{- end }}

{{/*
Expand the name of the chart.
*/}}
{{- define "zio-http-poc-chart.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "zio-http-poc-chart.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "zio-http-poc-chart.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "zio-http-poc-chart.labels" -}}
helm.sh/chart: {{ include "zio-http-poc-chart.chart" . }}
{{ include "zio-http-poc-chart.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "zio-http-poc-chart.selectorLabels" -}}
app.kubernetes.io/name: {{ include "zio-http-poc-chart.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

