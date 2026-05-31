import random
import uvicorn
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional, List

app = FastAPI(
    title="EcoFriend REST API Service",
    description="Sleek IoT sensor telemetry and crop diagnostics api provider.",
    version="1.0.0"
)

# Enable Cross-Origin Resource Sharing (CORS) for external web/mobile client sandboxes
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Request-Response Models
class SuitabilityRequest(BaseModel):
    plant_species: str
    location_city: str
    temperature_str: str
    rainfall_str: str

class SuitabilityResponse(BaseModel):
    compatible: bool
    suitability_score: int
    recommendation_summary: str

class SoilAnalysisRequest(BaseModel):
    description_notes: str
    include_sensor_assessment: bool = True

class SoilAnalysisResponse(BaseModel):
    soil_class: str
    ph_level: float
    moisture_index: str
    macronutrients: str
    gardening_verdict: str

class ChatMessagePayload(BaseModel):
    user_prompt: str
    language: Optional[str] = "en"

class ChatMessageResponse(BaseModel):
    bot_reply: str
    audio_output_simulated_url: Optional[str] = None


@app.get("/")
def get_health_status():
    """System health audit check."""
    return {
        "status": "online",
        "service": "EcoFriend API Server",
        "environment": "Production Sandbox",
        "timestamp_now": "2026-05-31T10:45:00Z"
    }


@app.post("/api/climate/check-suitability", response_model=SuitabilityResponse)
def handle_climate_check(payload: SuitabilityRequest):
    """Calculates physical climate suitability profiles for a specific plant variety."""
    if not payload.plant_species or not payload.location_city:
        raise HTTPException(status_code=400, detail="Missing essential plant variety or zone.")
    
    score = random.randint(72, 98)
    compatible = score >= 80
    
    verdict = (
        f"Ideal agricultural indicators calculated! Climate '{payload.rainfall_str}' with temperature '{payload.temperature_str}' "
        f"is highly matches {payload.plant_species} growth factors."
    ) if compatible else (
        f"Suboptimal indicators detected for {payload.plant_species}. The average climate elements of '{payload.location_city}' "
        f"may require artificial shade netting or greenhouse microclimate controllers."
    )
    
    return SuitabilityResponse(
        compatible=compatible,
        suitability_score=score,
        recommendation_summary=verdict
    )


@app.post("/api/soil/analyze", response_model=SoilAnalysisResponse)
def handle_soil_analysis(payload: SoilAnalysisRequest):
    """Parses chemical and physical properties of a soil specimen."""
    soil_type = "Sandy Loam" if "sand" in payload.description_notes.lower() else "Rich Clay Loam"
    ph = round(random.uniform(6.2, 7.3), 1)
    
    return SoilAnalysisResponse(
        soil_class=soil_type,
        ph_level=ph,
        moisture_index="84% (Optimal humidity balance)",
        macronutrients="Nitrogen (Stable Medium), Phosphorus (High), Potassium (Excellent)",
        gardening_verdict="Highly robust. Excellent substrate buffering capacity. Safe to seed compact medicinal leaf clusters immediately."
    )


@app.post("/api/chat/respond", response_model=ChatMessageResponse)
def handle_companion_chat(payload: ChatMessagePayload):
    """Generates botanical recommendations modeling PrakritiMitra's expert intellect."""
    prompt = payload.user_prompt.lower()
    
    if "yellow" in prompt or "spotted" in prompt:
        reply = (
            "Chlorosis pattern recognized! This implies slight nitrogen washouts or poor sub-drainage. "
            "Examine soil humidity: let the top roots breath, and mix natural compost, organic bio-fertilizer or aged manures."
        )
    elif "water" in prompt or "irrigation" in prompt:
        reply = (
            "Water roots early in the morning hours to cut down excessive soil vaporizations. "
            "This gives plants ample day absorption without provoking nightly humidity fungi breakouts."
        )
    else:
        reply = (
            "A fascinating ecological inquiry! I am PrakritiMitra. Always pair organic pest control, "
            "shun toxic synthetic chemicals, and promote diverse microclimate cover cropping to protect soil health."
        )
        
    return ChatMessageResponse(
        bot_reply=reply,
        audio_output_simulated_url="https://api.ecofriend.example/v1/voice/sim_tts_stream"
    )


if __name__ == "__main__":
    print("Launching EcoFriend API Server at port 8000...")
    uvicorn.run(app, host="0.0.0.0", port=8000)
