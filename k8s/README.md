# Kubernetes no crochetapp

## 1. Criar cluster kind com portas expostas no host

Se o cluster ja existir e tiver sido criado sem esse arquivo de configuracao, recrie:

```bash
kind delete cluster --name crochetapp
kind create cluster --config k8s/kind-config.yaml
```

## 2. Build das imagens

```bash
docker build -t discovery-server:1.0 -f discovery-server/Dockerfile .
docker build -t auth-service:1.0 -f services/auth-service/Dockerfile .
docker build -t catalog-service:1.0 -f services/catalog-service/Dockerfile .
docker build -t inventory-service:1.0 -f services/inventory-service/Dockerfile .
docker build -t budgeting-service:1.0 -f services/budgeting-service/Dockerfile .
docker build -t api-gateway:1.0 -f gateway/api-gateway/Dockerfile .
docker build -t frontend:1.0 -f frontend/Dockerfile ./frontend
```

## 3. Carregar no kind

```bash
for image in discovery-server auth-service catalog-service inventory-service budgeting-service api-gateway frontend; do
  kind load docker-image "$image:1.0" --name crochetapp
done
```

## 4. Aplicar os manifests

```bash
kubectl apply -f k8s/
```

## 5. Acompanhar status dos pods

```bash
kubectl get pods -n crochetapp -w
```

## 6. Acessar a aplicação sem port-forward

Como o frontend foi buildado com `NEXT_PUBLIC_API_URL=http://localhost:8080`, o acesso local fica:

```bash
# Frontend
http://localhost:3000

# API Gateway
http://localhost:8080
```

## 7. Secret do JWT

O valor em `k8s/01-secrets.yaml` é apenas um placeholder de desenvolvimento.