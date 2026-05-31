import streamlit as st
import random
import time

# Set page configuration with a green leaf emoji and sleek layout
st.set_page_config(
    page_title="EcoFriend Web Companion",
    page_icon="🌱",
    layout="wide",
    initial_sidebar_state="expanded"
)

# Custom Sleek Theme CSS injection
st.markdown("""
<style>
    /* Styling elements to match Sleek Theme (#F3F5F1, #2E7D32, #FFFFFF) */
    .stApp {
        background-color: #F3F5F1;
        color: #1E293B;
    }
    /* Typography customization */
    h1, h2, h3 {
        font-family: 'Playfair Display', 'Georgia', serif;
        color: #1E293B;
    }
    p, span, label {
        font-family: 'Inter', sans-serif;
    }
    /* Cards and boxes styling */
    .custom-card {
        background-color: #FFFFFF;
        padding: 1.5rem;
        border-radius: 16px;
        border: 1px solid #E2E8F0;
        box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        margin-bottom: 1rem;
    }
    /* Hero card style */
    .hero-card {
        background-color: #2E7D32;
        color: white;
        padding: 2.25rem;
        border-radius: 28px;
        position: relative;
        overflow: hidden;
        margin-bottom: 1.5rem;
        box-shadow: 0 4px 12px rgba(46, 125, 50, 0.15);
    }
    .hero-title {
        font-size: 2rem;
        font-weight: 800;
        margin-bottom: 0.5rem;
        font-family: 'Playfair Display', serif;
    }
    .hero-subtitle {
        font-size: 1rem;
        opacity: 0.95;
        margin-bottom: 1.5rem;
        max-width: 75%;
    }
    .badge {
        background-color: rgba(255, 255, 255, 0.2);
        color: white;
        padding: 0.25rem 0.75rem;
        border-radius: 9999px;
        font-size: 0.75rem;
        font-weight: bold;
        text-transform: uppercase;
        display: inline-block;
        margin-bottom: 1rem;
        letter-spacing: 0.05em;
    }
    .badge-yellow {
        background-color: #FFD54F;
        color: #795548;
        padding: 0.4rem 1rem;
        border-radius: 12px;
        font-size: 0.85rem;
        font-weight: bold;
        display: inline-block;
        box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }
</style>
""", unsafe_allow_html=True)

# Sidebar with Brand and Developer Information
with st.sidebar:
    st.markdown("<h2 style='color:#2E7D32; margin-top:0;'>🌱 EcoFriend</h2>", unsafe_allow_html=True)
    st.caption("AI-Powered Ecological Assistant")
    st.markdown("---")
    
    st.subheader("📍 Ambient Atmosphere")
    st.info("Hyderabad, IN • 28°C • Sunny ☀️")
    
    st.subheader("🛡️ Gamification Tracker")
    st.markdown("""
    🏆 **Level 4: Tree Guardian**
    - **Current Score**: 880 GP (*Green Points*)
    - **Target**: 120 GP to become *Forest Master*
    """)
    st.progress(0.88)
    
    st.markdown("---")
    st.caption("Made with ♥ for EcoFriend Sandbox")

# Header Bar
col1, col2 = st.columns([3, 1])
with col1:
    st.markdown("<h1 style='margin-bottom:0;'>EcoFriend Dashboard</h1>", unsafe_allow_html=True)
    st.markdown("<p style='color:#64748B; font-size:0.95rem; margin-top:0.2rem;'>Sleek Web Interface Companion</p>", unsafe_allow_html=True)
with col2:
    st.write("")

# Tab Navigation
tab_home, tab_diagnose, tab_mitra, tab_stats = st.tabs([
    "🏠 Home Hub", "🔍 AI Leaf & Soil Diagnostician", "🤖 PrakritiMitra Chat", "📈 Growth Analytics"
])

# ==========================================
# 🏠 HOME TAB
# ==========================================
with tab_home:
    # Hero Card
    st.markdown("""
    <div class="hero-card">
        <span class="badge">AI Recommendation</span>
        <div class="hero-title">Planting Season: Tulsi (Holy Basil)</div>
        <div class="hero-subtitle">Ideal microclimate, loamy soil substrate readiness and high ambient humidity detected. Exceptional choice for natural oxygenation, medicinal benefits, and compact balconies.</div>
        <div class="badge-yellow">🎯 Climate Match Score: 98%</div>
    </div>
    """, unsafe_allow_html=True)

    # Stats and Metrics grid
    col_metric1, col_metric2 = st.columns(2)
    with col_metric1:
        st.markdown("""
        <div class="custom-card">
            <div style="display:flex; justify-content:space-between; align-items:center;">
                <span style="font-size:0.85rem; color:#64748B; font-weight:500;">Soil Health Index</span>
                <span style="font-size:0.8rem; color:#16A34A; font-weight:bold; background-color:#DCFCE7; padding:0.1rem 0.5rem; border-radius:6px;">Optimal</span>
            </div>
            <div style="display:flex; align-items:baseline; margin-top:1rem; gap:10px;">
                <span style="font-size:2rem; font-weight:bold; color:#1E293B;">84%</span>
                <span style="font-size:0.85rem; color:#64748B;">Moisture Stable</span>
            </div>
            <div style="background-color:#F1F5F9; height:8px; width:100%; border-radius:999px; margin-top:0.5rem; overflow:hidden;">
                <div style="background-color:#81C784; height:100%; width:84%;"></div>
            </div>
        </div>
        """, unsafe_allow_html=True)

    with col_metric2:
        st.markdown("""
        <div class="custom-card">
            <div style="display:flex; justify-content:space-between; align-items:center;">
                <span style="font-size:0.85rem; color:#64748B; font-weight:500;">Biomass Growth Score</span>
                <span style="font-size:0.8rem; color:#2563EB; font-weight:bold; background-color:#DBEAFE; padding:0.1rem 0.5rem; border-radius:6px;">+4.2% weekly</span>
            </div>
            <div style="display:flex; align-items:baseline; margin-top:1rem; gap:2px;">
                <span style="font-size:2rem; font-weight:bold; color:#1E293B;">7.2</span>
                <span style="font-size:0.85rem; color:#94A3B8;">/10 Integrity</span>
            </div>
            <div style="display:flex; gap:3px; margin-top:0.75rem; align-items:flex-end; height:18px;">
                <div style="height:6px; width:10px; background-color:#4FC3F7; opacity:0.3; border-radius:2px;"></div>
                <div style="height:10px; width:10px; background-color:#4FC3F7; opacity:0.5; border-radius:2px;"></div>
                <div style="height:16px; width:10px; background-color:#4FC3F7; opacity:0.8; border-radius:2px;"></div>
                <div style="height:12px; width:10px; background-color:#4FC3F7; border-radius:2px;"></div>
            </div>
        </div>
        """, unsafe_allow_html=True)

    # Climate Suitability Checker
    st.subheader("📊 Interactive Climate Suitability Index")
    st.write("Determine if a specific plant variety is compatible with your backyard's dynamic microclimatic variables.")
    
    col_sc1, col_sc2 = st.columns(2)
    with col_sc1:
        check_plant = st.text_input("Plant Species", "Tomato Vining F1")
        check_zone = st.text_input("Target City/Zone", "Hyderabad Hub")
    with col_sc2:
        check_temp = st.text_input("Average Temperature Estimate", "30°C")
        check_rain = st.selectbox("Rainfall or Irrigation Level", ["Low", "Medium Average", "High Volume"])
        
    if st.button("Calculate Climate Suitability"):
        with st.spinner("Analyzing atmospheric metadata..."):
            time.sleep(1.2)
            score = random.randint(78, 96)
            st.success(f"✔️ Climate Compatibility Score: {score}%! Perfect matching for {check_plant} cultivation.")
            st.info(f"💡 Recommendation: For best yield in '{check_zone}' with temperature around {check_temp}, prepare organic loam substrate with high potassium/phosphorus concentrations.")

# ==========================================
# 🔍 DIAGNOSTIC TAB
# ==========================================
with tab_diagnose:
    st.subheader("🌿 Sickness Scanner & Substrate Analyzer")
    st.write("Diagnose leaf pathogens or decode physical soil properties using Gemini-powered computer vision.")
    
    subtab_leaf, subtab_soil = st.tabs(["🍃 Leaf Diagnosis", "🪨 Soil Analysis"])
    
    with subtab_leaf:
        st.write("Upload an image of a discolored leaf to detect leaf rust, early blight, nutrient deficiencies, and receive organic remedies.")
        uploaded_leaf = st.file_uploader("Upload Leaf Snapshot", type=["jpg", "png", "jpeg"], key="leaf_file")
        st.write("- OR -")
        demo_case = st.selectbox("Run Instant Diagnostic Simulation Case", [
            "Select Scenario...",
            "Wheat Spotted Rust (Severe)",
            "Tomato Solani Early Blight (Moderate)",
            "Citrus Dry Leaf Spotting (Mild)"
        ])
        
        if uploaded_leaf is not None or demo_case != "Select Scenario...":
            with st.spinner("Processing pathobiology patterns via Gemini Vision API..."):
                time.sleep(1.5)
                
                if demo_case == "Wheat Spotted Rust (Severe)" or (uploaded_leaf and "rust" in uploaded_leaf.name.lower()):
                    st.error("🚨 Pathogen Diagnosed: Wheat Yellow/Stripe Rust (*Puccinia striiformis*)")
                    st.markdown("""
                    **Diagnostics Details:**
                    - **Severity Index**: **HIGH** (Spreading rapidly, covers ~45% of foliar area)
                    - **Etiology**: High moisture retention coupled with low nocturnal temperatures.
                    
                    **Organic Remedies:**
                    1. Dust leaves with organic agricultural sulfur powder.
                    2. Maintain proper row-spacing to optimize canopy ventilation.
                    3. Actively prune heavily infected leaves and incinerate offsite.
                    """)
                elif demo_case == "Tomato Solani Early Blight (Moderate)" or (uploaded_leaf and "tomato" in uploaded_leaf.name.lower()):
                    st.warning("⚠️ Pathogen Diagnosed: Tomato Solani Early Blight (*Alternaria solani*)")
                    st.markdown("""
                    **Diagnostics Details:**
                    - **Severity Index**: **MODERATE** (Target-like concentric ring brown lesions)
                    - **Etiology**: Water splashes transporting microscopic spores from soil surface to lower leaves.
                    
                    **Organic Remedies:**
                    1. Apply organic organic copper fungicide or neem oil solution every 7 days.
                    2. Drip-irrigate roots directly; avoid standard overhead sprinkler systems.
                    3. Mulch the base soil heavily to prevent spores from splashing onto foliage.
                    """)
                else:
                    st.success("🟢 Diagnosed: Mild Citrus Dry Spot / Abiotic Stress")
                    st.markdown("""
                    **Diagnostics Details:**
                    - **Severity Index**: **MILD/LOW**
                    - **Etiology**: Superficial sunburn spots due to wind-drying under extreme UV indices. Non-infectious.
                    
                    **Organic Remedies:**
                    1. Install simple shaded green netting over the saplings.
                    2. Mist water during midday dry heat peaks to maintain microclimatic humidity.
                    """)
                    
    with subtab_soil:
        st.write("Analyze natural soil texture, moisture, and estimate substrate composition metrics.")
        soil_desc = st.text_area("Substrate Manual Notes / Location Context", 
                                 placeholder="Red volcanic gravelly ground, dry texture, backyard patio near dense foliage. Preparing to plant medical Holy Basil.")
        uploaded_soil = st.file_uploader("Upload Substrate Photo", type=["jpg", "png", "jpeg"], key="soil_file")
        
        if st.button("Run Substrate Evaluation"):
            with st.spinner("Decomposing substrate visual features..."):
                time.sleep(1.0)
                st.success("🤖 Substrate Breakdown Report Generated!")
                st.markdown(f"""
                - **Substrate Assessment**: Sand-Clay loam mixture with medium-high organic humus content.
                - **Porosity Estimate**: High drainage rates, low waterlogging risk.
                - **Estimated pH Target**: 6.4 (Slightly Acidic - ideal for most medicinal herbs).
                - **Actionable AI Tip**: Add 20% vermicompost to bulk the water retention.
                """)

# ==========================================
# 🤖 CHAT TAB
# ==========================================
with tab_mitra:
    st.markdown("""
    <div style="background-color:#E8F5E9; padding:1.25rem; border-radius:18px; border:1px solid #C8E6C9; margin-bottom:1.5rem;">
        <h4 style="color:#2E7D32; margin:0 0 0.25rem 0;">PrakritiMitra Voice Companion Active</h4>
        <p style="margin:0; font-size:0.9rem; color:#1B5E20;">Ask PrakritiMitra anything about crop rotations, home nursery setups, organic bio-pesticides, and climate resilience practices.</p>
    </div>
    """, unsafe_allow_html=True)

    # Initialize chat state
    if "messages" not in st.session_state:
        st.session_state.messages = [
            {"role": "assistant", "content": "Welcome back, EcoFriend! 🌿 I am PrakritiMitra. Ask me any botanical or agricultural question to grow a greener future."}
        ]

    # Render previous chats
    for msg in st.session_state.messages:
        with st.chat_message(msg["role"]):
            st.write(msg["content"])

    # Chat input
    user_chat = st.chat_input("Speak or Type botanical question to PrakritiMitra...")
    
    if user_chat:
        st.session_state.messages.append({"role": "user", "content": user_chat})
        with st.chat_message("user"):
            st.write(user_chat)
            
        with st.chat_message("assistant"):
            with st.spinner("PrakritiMitra is drafting botanical advice... 🧠🌱"):
                time.sleep(0.8)
                
                # Dynamic response logic helper
                u_lower = user_chat.lower()
                if "yellow" in u_lower or "leaves" in u_lower:
                    ans = "Yellowing leaves (chlorosis) usually signal nitrogen deficiency or overwatering. Let's inspect the soil moisture: if the top 2 inches of soil are dry, supply organic compost or balanced organic liquid nitrogen fertilizers!"
                elif "water" in u_lower or "schedule" in u_lower:
                    ans = "Watering is optimal early in the morning before dawn! This saves up to 30% evaporation loss and prevents nocturnal fungal propagation on wet leaf surfaces."
                else:
                    ans = "A fascinating crop biology inquiry! I recommend maintaining porous soil substrates rich in active microbial humuses. Would you like to check our climate suitability checker to audit its compatibility?"
                
                st.session_state.messages.append({"role": "assistant", "content": ans})
                st.write(ans)

# ==========================================
# 📈 STATISTICS TAB
# ==========================================
with tab_stats:
    st.subheader("📊 Micro-environment Bio-Sensors & Growth logs")
    
    # Generated random chart data
    chart_data = [random.randint(60, 95) for _ in range(12)]
    st.line_chart(chart_data)
    
    st.markdown("""
    ### 📝 Quick Garden Logs
    - **Tulsi Holy Basil Nursery**: Transformed into self-watering loam pot on 2026-05-31. Growth index: **Excellent**.
    - **Yellowing Leaves incident**: Remedied by reducing overhead spray water volumes and adding composted cow-manures.
    """)
